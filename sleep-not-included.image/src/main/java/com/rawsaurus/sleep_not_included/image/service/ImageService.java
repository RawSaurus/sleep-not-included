package com.rawsaurus.sleep_not_included.image.service;

import com.rawsaurus.sleep_not_included.image.client.BuildClient;
import com.rawsaurus.sleep_not_included.image.client.GameResClient;
import com.rawsaurus.sleep_not_included.image.client.UserClient;
import com.rawsaurus.sleep_not_included.image.dto.DeleteEntityEvent;
import com.rawsaurus.sleep_not_included.image.dto.ImageResponse;
import com.rawsaurus.sleep_not_included.image.dto.OwnerData;
import com.rawsaurus.sleep_not_included.image.dto.UpdateImageUrlEvent;
import com.rawsaurus.sleep_not_included.image.handler.ActionNotAllowed;
import com.rawsaurus.sleep_not_included.image.handler.StorageException;
import com.rawsaurus.sleep_not_included.image.mapper.ImageMapper;
import com.rawsaurus.sleep_not_included.image.model.Image;
import com.rawsaurus.sleep_not_included.image.model.ImageType;
import com.rawsaurus.sleep_not_included.image.repo.ImageRepository;
import io.minio.*;
import jakarta.persistence.EntityNotFoundException;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.rawsaurus.sleep_not_included.image.config.RabbitMQConfig.*;
import static com.rawsaurus.sleep_not_included.image.model.ImageType.BUILD_IMAGE;
import static com.rawsaurus.sleep_not_included.image.model.ImageType.PROFILE_PIC;

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

    private final RabbitTemplate rabbitTemplate;

    @Value("${sni.minio.url}")
    private String minioUrl;
    @Value("${sni.minio.bucket}")
    private String bucket;

    public ImageService(ImageRepository imageRepo, ImageMapper imageMapper, UserClient userClient, BuildClient buildClient, GameResClient resClient, MinioClient minioClient, RabbitTemplate rabbitTemplate) {
        this.imageRepo= imageRepo;
        this.imageMapper = imageMapper;
        this.userClient = userClient;
        this.buildClient = buildClient;
        this.resClient = resClient;
        this.minioClient = minioClient;
        this.rabbitTemplate = rabbitTemplate;
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

    @Transactional
    public ImageResponse uploadImage(MultipartFile file, ImageType type, String name){
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

        if(type == PROFILE_PIC) {
            Image savedImage = imageRepo.save(image);

            sendProfilePicUrlToUser(owner.id(), savedImage.getUrl());

            return imageMapper.toResponse(
                    savedImage
            );
        }

        return imageMapper.toResponse(
                imageRepo.save(image)
        );
    }

    @Transactional
    public List<ImageResponse> uploadBuildImages(List<MultipartFile> files, String name){
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
    public ImageResponse updateImage(MultipartFile file, ImageType type, String name) {
        if (file.isEmpty())
            throw new StorageException("No image provided");
        if(!file.getContentType().equals("image/jpeg"))
            throw new StorageException("Wrong content type");

        OwnerData owner = checkOwner(type, name);

        Optional<Image> imageToSave = imageRepo
                .findImageByTypeAndOwnerId(type, owner.id());

        //not sure about this. Test more
        if(imageToSave.isEmpty()){
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

            Image savedImage = imageRepo.save(image);

            sendProfilePicUrlToUser(owner.id(), savedImage.getUrl());

            return imageMapper.toResponse(
                    savedImage
            );
        }

        byte[] processedImage = resizeImage(file, owner.width(), owner.height());
        uploadToMinio(imageToSave.get().getStoragePath(), processedImage, "image/jpeg");

        imageToSave.get().setFilename(file.getOriginalFilename());
        imageToSave.get().setSize(file.getSize());

        Image savedImage = imageRepo.save(imageToSave.get());

        sendProfilePicUrlToUser(owner.id(), savedImage.getUrl());

        return imageMapper.toResponse(
                savedImage
        );
    }

    //TODO check if user logged in is owner
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
//                    .crop(Positions.CENTER)
                    .keepAspectRatio(true)
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

    private void sendProfilePicUrlToUser(Long userId, String profilePicUrl){

        rabbitTemplate.convertAndSend(
                IMAGE_UPDATE_ROUTING_KEY,
                new UpdateImageUrlEvent(userId, profilePicUrl)
        );

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