package com.rawsaurus.sleep_not_included.image.service;

import com.rawsaurus.sleep_not_included.image.client.BuildClient;
import com.rawsaurus.sleep_not_included.image.client.GameResClient;
import com.rawsaurus.sleep_not_included.image.client.UserClient;
import com.rawsaurus.sleep_not_included.image.config.RabbitMQConfig;
import com.rawsaurus.sleep_not_included.image.dto.DeleteEntityEvent;
import com.rawsaurus.sleep_not_included.image.dto.ImageResponse;
import com.rawsaurus.sleep_not_included.image.dto.OwnerData;
import com.rawsaurus.sleep_not_included.image.handler.ActionNotAllowed;
import com.rawsaurus.sleep_not_included.image.handler.StorageException;
import com.rawsaurus.sleep_not_included.image.mapper.ImageMapper;
import com.rawsaurus.sleep_not_included.image.model.Image;
import com.rawsaurus.sleep_not_included.image.model.ImageType;
import com.rawsaurus.sleep_not_included.image.repo.ImageRepository;
import io.minio.*;
import io.minio.errors.*;
import jakarta.persistence.EntityNotFoundException;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.rawsaurus.sleep_not_included.image.config.RabbitMQConfig.*;
import static com.rawsaurus.sleep_not_included.image.model.ImageType.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class ImageService {

    private static final Path ROOT_LOCATION = Paths.get("./sleep-not-included.image/Files-Upload");
    private static final Path USER_LOCATION = Paths.get(ROOT_LOCATION.toString(), "/user");
    private static final Path BUILD_THUMBNAIL_LOCATION = Paths.get(ROOT_LOCATION.toString(), "/build-thumb");
    private static final Path BUILD_IMAGE_LOCATION = Paths.get(ROOT_LOCATION.toString(), "/build");
    private static final Path RES_IMAGE_LOCATION = Paths.get(ROOT_LOCATION.toString(), "/res");

    private final ImageRepository imageRepo;

    private final ImageMapper imageMapper;

    private final UserClient userClient;
    private final BuildClient buildClient;
    private final GameResClient resClient;
    private final MinioClient minioClient;

    @Value("${sni.minio.url}")
    private String minioUrl;
    @Value("${sni.minio.bucket}")
    private String bucket;

    public ImageService(ImageRepository imageRepo, ImageMapper imageMapper, UserClient userClient, BuildClient buildClient, GameResClient resClient, MinioClient minioClient) {
        this.imageRepo= imageRepo;
        this.imageMapper = imageMapper;
        this.userClient = userClient;
        this.buildClient = buildClient;
        this.resClient = resClient;
        this.minioClient = minioClient;
        createDir();
    }

    public ImageResponse findById(Long id){
        return imageMapper.toResponse(
                imageRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Image not found"))
        );
    }

    public List<ImageResponse> findByOwner(String ownerService, Long ownerId){
        return imageRepo.findAllByOwnerServiceIgnoreCaseAndOwnerId(ownerService, ownerId)
                .stream()
                .map(imageMapper::toResponse)
                .toList();
    }

    public List<String> findUrlByOwner(String ownerService, Long ownerId){
        return imageRepo.findAllByOwnerServiceIgnoreCaseAndOwnerId(ownerService, ownerId)
                .stream()
                .map(Image::getUrl)
                .toList();
    }

    public List<ImageResponse> findAllByOwnerIds(String ownerService, List<Long> ids){
        return imageRepo.findAllByOwnerServiceIgnoreCaseAndOwnerIdIn(ownerService, ids)
                .stream()
                .map(imageMapper::toResponse)
                .toList();
    }

    public List<String> findAllUrlByOwnerIds(String ownerService, List<Long> ids){
        return imageRepo.findAllByOwnerServiceIgnoreCaseAndOwnerIdIn(ownerService, ids)
                .stream()
                .map(Image::getUrl)
                .toList();
    }

    public Resource downloadImage(ImageType type, String name){

        OwnerData ownerData = checkOwner(type, name);

        Image image = imageRepo.findImageByStoragePathAndOwnerId(ownerData.location().toString(), ownerData.id())
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        try{
            Path filePath = ownerData.location().resolve(image.getId().toString() + ".jpeg");
            System.out.print(filePath);
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()){
                return resource;
            }else{
                throw new StorageException("File " + filePath + " could not be found");
            }
        } catch(Exception e){
            throw new StorageException(e.getMessage());
        }
    }

    public MultiValueMap<String, Object> downloadBuildImages(String name){
        ImageType type = BUILD_IMAGE;
        OwnerData ownerData = checkOwner(type, name);

        List<Image> images = imageRepo.findAllByStoragePathAndOwnerId(ownerData.location().toString(), ownerData.id());
        MultiValueMap<String, Object> resources = new LinkedMultiValueMap<>();

        for(Image image : images) {
            try {
                Path filePath = ownerData.location().resolve(image.getId().toString() + ".jpeg");
                System.out.print(filePath);
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    HttpHeaders partHeaders = new HttpHeaders();
                    partHeaders.setContentType(MediaType.IMAGE_JPEG);
                    resources.add("images", new HttpEntity<>(resource, partHeaders));
//                    resources.add(resource);
                } else {
                    throw new StorageException("File " + filePath + " could not be found");
                }
            } catch (Exception e) {
                throw new StorageException(e.getMessage());
            }
        }
        return resources;
    }

    public Resource downloadBuildThumbnail(Long id){
        var build = buildClient.findById(id).getBody();
        if(build == null){
            throw new EntityNotFoundException("Build not found");
        }

        Image image = imageRepo.findImageByStoragePathAndOwnerId(BUILD_THUMBNAIL_LOCATION.toString(), build.id())
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        try{
            Path filePath = BUILD_THUMBNAIL_LOCATION.resolve(image.getId().toString()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()){
                return resource;
            }else{
                throw new StorageException("File could not be read");
            }
        } catch(Exception e){
            throw new StorageException("File could not be read");
        }
    }

    @Transactional
    public ImageResponse uploadTestImage(MultipartFile file, ImageType type, String name){
        if(file.isEmpty()){
            throw new StorageException("No image provided");
        }
        if(!file.getContentType().equals("image/jpeg")){
            throw new StorageException("Wrong content type");
        }
        OwnerData owner = checkOwner(type, name);
        String imageKey = buildObjectKey(type, owner.id(), file.getOriginalFilename());

        byte[] processedImage = resizeImage(file, owner.width(), owner.height());
        uploadToMinio(imageKey, processedImage, "image/jpeg");

        String url = buildUrl(imageKey);

        Image image = Image.builder()
                .filename(file.getOriginalFilename())
                .type(type)
                .size(file.getSize())
                .storagePath(imageKey)
                .url(url)
                .ownerService(owner.ownerService())
                .ownerId(owner.id())
                .build();

        return imageMapper.toResponse(
                imageRepo.save(image)
        );
    }

    @Transactional
    public List<ImageResponse> uploadTestBuildImages(List<MultipartFile> files, String name){
        if(files.getFirst().isEmpty()){
            throw new StorageException("No images provided");
        }

        OwnerData owner = checkOwner(BUILD_IMAGE, name);

        return files.stream().map(
                file -> {
                    String imageKey = buildObjectKey(BUILD_IMAGE, owner.id(), file.getOriginalFilename());
                    byte[] processedImage = resizeImage(file, owner.width(), owner.height());
                    uploadToMinio(imageKey, processedImage, "image/jpeg");

                    String url = buildUrl(imageKey);

                    Image image = Image.builder()
                            .filename(file.getOriginalFilename())
                            .type(BUILD_IMAGE)
                            .size(file.getSize())
                            .storagePath(imageKey)
                            .url(url)
                            .ownerService(owner.ownerService())
                            .ownerId(owner.id())
                            .build();

                    return imageMapper.toResponse(
                            imageRepo.save(image)
                    );
                }).toList();
    }

    @Transactional
    public String uploadImage(MultipartFile file, ImageType type, String name){
        if(file.isEmpty()){
            throw new StorageException("No image provided");
        }
        if(!file.getContentType().equals("image/jpeg")){
            throw new StorageException("Wrong content type");
        }

        OwnerData ownerData = checkOwner(type, name);

        var checkImage = imageRepo.findImageByStoragePathAndOwnerId(ownerData.location().toString(), ownerData.id());

        if(checkImage.isPresent()){
            throw new ActionNotAllowed("Image already exists");
        }

        Image imageToSave = Image.builder()
                .filename(file.getOriginalFilename())
                .type(type)
                .size(file.getSize())
                .storagePath(ownerData.location().toString())
                .ownerService(ownerData.ownerService())
                .ownerId(ownerData.id())
                .build();

        Image image = imageRepo.save(imageToSave);

        try (InputStream input = file.getInputStream()){
            Path path = ownerData.location().resolve(image.getId().toString());
            Thumbnails.of(input)
                    .size(480, 320)
                    .crop(Positions.CENTER)
                    .outputFormat("jpeg")
                    .outputQuality(0.85)
                    .toFile(path.toFile());
//            Files.copy(input, path, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
        return "File stored successfully";
    }

    @Transactional
    public String uploadBuildImages(List<MultipartFile> files, String name){
        if(files.get(0).isEmpty()){
            throw new StorageException("No image provided");
        }

        for (MultipartFile file : files) {
            if (!file.getContentType().equals("image/jpeg")) {
                throw new StorageException("Wrong content type");
            }
        }
        ImageType type = BUILD_IMAGE;
        OwnerData ownerData = checkOwner(type, name);

//        var checkImage = imageRepo.findImageByStoragePathAndOwnerId(ownerData.location().toString(), ownerData.id());

//        if(checkImage.isPresent()){
//            throw new ActionNotAllowed("Image already exists");
//        }

        for(MultipartFile file : files) {
            Image imageToSave = Image.builder()
                    .filename(file.getOriginalFilename())
                    .type(type)
                    .size(file.getSize())
                    .storagePath(ownerData.location().toString())
                    .ownerService(ownerData.ownerService())
                    .ownerId(ownerData.id())
                    .build();

            Image image = imageRepo.save(imageToSave);

            if(image != null){
                try (InputStream input = file.getInputStream()) {
                    Path path = ownerData.location().resolve(image.getId().toString());
                    Thumbnails.of(input)
                            .size(480, 320)
                            .crop(Positions.CENTER)
                            .outputFormat("jpeg")
                            .outputQuality(0.85)
                            .toFile(path.toFile());
    //            Files.copy(input, path, REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new StorageException(e.getMessage());
                }
            }
        }
        return "File stored successfully";
    }

    @Transactional
    public ImageResponse updateTestImage(MultipartFile file, ImageType type, String name) {
        if (file.isEmpty())
            throw new StorageException("No image provided");
        if(!file.getContentType().equals("image/jpeg"))
            throw new StorageException("Wrong content type");

        OwnerData owner = checkOwner(type, name);

        Image imageToSave = imageRepo
                .findImageByStoragePathAndOwnerId(buildObjectKeyPrefix(type, owner.id()), owner.id())
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        byte[] processedImage = resizeImage(file, owner.width(), owner.height());
        uploadToMinio(imageToSave.getStoragePath(), processedImage, "image/jpeg");

        imageToSave.setFilename(file.getOriginalFilename());
        imageToSave.setSize(file.getSize());

        return imageMapper.toResponse(imageRepo.save(imageToSave));
    }

    @Transactional
    public String updateImage(MultipartFile file, ImageType type, String name){

        if(!file.getContentType().equals("image/jpeg")){
            throw new StorageException("Wrong content type");
        }

        OwnerData ownerData = checkOwner(type, name);

        Image imageToSave = imageRepo.findImageByStoragePathAndOwnerId(ownerData.location().toString(), ownerData.id())
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        imageToSave.setFilename(file.getOriginalFilename());
        imageToSave.setType(type);
        imageToSave.setSize(file.getSize());
        imageToSave.setStoragePath(ownerData.location().toString());
        imageToSave.setOwnerService(ownerData.ownerService());
        imageToSave.setOwnerId(ownerData.id());

        Image image = imageRepo.save(imageToSave);


        try (InputStream input = file.getInputStream()){
            Path path = ownerData.location().resolve(image.getId().toString());
            Files.copy(input, path, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
        return "File stored successfully";
    }


    public String deleteFile(String serviceName, Long ownerId){
        deleteFilesInternal(serviceName, ownerId);
        return "File deleted";
    }

    private String buildObjectKey(ImageType type, Long ownerId, String filename) {
        return type.name().toLowerCase() + "/" + ownerId + "/" + System.currentTimeMillis() + ".jpeg";
    }

    private String buildObjectKeyPrefix(ImageType type, Long ownerId) {
        return type.name().toLowerCase() + "/" + ownerId + "/";
    }

    private String buildUrl(String objectKey) {
        return minioUrl + "/" + bucket + "/" + objectKey;
    }

    private byte[] resizeImage(MultipartFile file, int width, int height){
        try (InputStream input = file.getInputStream()){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Thumbnails.of(input)
                    .size(width, height)
                    .crop(Positions.CENTER)
                    .outputFormat("jpeg")
                    .outputQuality(0.85)
                    .toOutputStream(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
    }

    @RabbitListener(queues = {IMAGE_USER_DELETED_QUEUE, IMAGE_BUILD_DELETED_QUEUE})
    @Transactional
    public void deleteFile(DeleteEntityEvent event){
        deleteFilesInternal(event.serviceName(), event.id());
    }

    public void deleteFilesInternal(String ownerService, Long ownerId) {
        List<Image> images = imageRepo.findAllByOwnerServiceIgnoreCaseAndOwnerId(ownerService, ownerId);
        for (Image image : images) {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(image.getStoragePath())
                        .build());
            } catch (Exception e) {
                throw new StorageException("Failed to delete file: " + e.getMessage());
            }
            imageRepo.delete(image);
        }
    }

    private void deleteFilesInternalOld(String serviceName, Long ownerId){
        List<Image> imgsToDelete = imageRepo.findAllByOwnerServiceIgnoreCaseAndOwnerId(serviceName, ownerId);

        imageRepo.deleteAll(imgsToDelete);

        try {
            switch (serviceName.toLowerCase()) {
                case "user" -> {
                    for (Image i : imgsToDelete) {
                        Files.deleteIfExists(USER_LOCATION.resolve(i.getId().toString() +".jpeg"));
                    }
                }
                case "build" -> {
                    for (Image i : imgsToDelete) {
                        Files.deleteIfExists(BUILD_THUMBNAIL_LOCATION.resolve(i.getId().toString() +".jpeg"));
                        Files.deleteIfExists(BUILD_IMAGE_LOCATION.resolve(i.getId().toString() +".jpeg"));
                    }
                }
                case "res" -> {
                    for (Image i : imgsToDelete) {
                        Files.deleteIfExists(RES_IMAGE_LOCATION.resolve(i.getId().toString() +".jpeg"));
                    }
                }
                default -> throw new StorageException("Wrong service name");
            }
        } catch (IOException e){
            throw new StorageException("File wasn't found", e.getCause());
        }
    }

    private void createDir(){
        try {
            if(!Files.exists(ROOT_LOCATION)) {
                Files.createDirectory(ROOT_LOCATION);
                Files.createDirectory(USER_LOCATION);
                Files.createDirectory(BUILD_IMAGE_LOCATION);
                Files.createDirectory(BUILD_THUMBNAIL_LOCATION);
                Files.createDirectory(RES_IMAGE_LOCATION);
            }
        } catch (IOException e) {
            throw new StorageException("Could not create directory " + ROOT_LOCATION);
        }
    }

    private void uploadToMinio(String imageKey, byte[] data, String contentType){
        try{
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if(!bucketExists){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                String policy = """
                    {"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},"Action":["s3:GetObject"],"Resource":["arn:aws:s3:::%s/*"]}]}
                    """.formatted(bucket);
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
            }
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(imageKey)
                            .stream(new ByteArrayInputStream(data), data.length, -1)
                            .contentType(contentType)
                            .build());
        } catch (Exception e) {
            throw new StorageException("Couldn't locate bucket because: " + e.getMessage());
        }
    }

    private OwnerData checkOwner(ImageType type, String name){
        OwnerData ownerData;
        switch(type){
            case PROFILE_PIC -> {
                var user = userClient.findUserByName(name).getBody();
                if(user == null){
                    throw new EntityNotFoundException("User not found");
                }
                ownerData = new OwnerData(user.id(), user.username(), "user", USER_LOCATION, 480, 360);
            }
            case RES_IMAGE -> {
                var gameres = resClient.findByName(name).getBody();
                if(gameres == null){
                    throw new EntityNotFoundException("Resource not found");
                }
                ownerData = new OwnerData(gameres.id(), gameres.name(), "gameres", RES_IMAGE_LOCATION, 480, 360);
            }
            case BUILD_IMAGE, BUILD_THUMBNAIL -> {
                var build = buildClient.findByName(name).getBody();
                if(build == null){
                    throw new EntityNotFoundException("Build not found");
                }
                if(type == BUILD_IMAGE){
                    ownerData = new OwnerData(build.id(), build.name(), "build", BUILD_IMAGE_LOCATION, 900, 700);
                }else{
                    ownerData = new OwnerData(build.id(), build.name(), "build", BUILD_THUMBNAIL_LOCATION, 480, 360);
                }
            }
            default -> throw new ActionNotAllowed("Owner of image couldn't be located");
        }
        return ownerData;
    }
}