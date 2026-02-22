package com.rawsaurus.sleep_not_included.image.service;

import com.rawsaurus.sleep_not_included.image.client.BuildClient;
import com.rawsaurus.sleep_not_included.image.client.UserClient;
import com.rawsaurus.sleep_not_included.image.dto.DeleteEntityEvent;
import com.rawsaurus.sleep_not_included.image.dto.ImageResponse;
import com.rawsaurus.sleep_not_included.image.dto.OwnerData;
import com.rawsaurus.sleep_not_included.image.handler.ActionNotAllowed;
import com.rawsaurus.sleep_not_included.image.handler.StorageException;
import com.rawsaurus.sleep_not_included.image.mapper.ImageMapper;
import com.rawsaurus.sleep_not_included.image.model.Image;
import com.rawsaurus.sleep_not_included.image.model.ImageType;
import com.rawsaurus.sleep_not_included.image.repo.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.rawsaurus.sleep_not_included.image.model.ImageType.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class ImageService {

    private static final String queueName = "image.entity.deleted.queue";
    private String exchangeName = "entity.events";
    private String routingKey = "entity.deleted";

    private static final Path ROOT_LOCATION = Paths.get("./sleep-not-included.image/Files-Upload");
    private static final Path USER_LOCATION = Paths.get(ROOT_LOCATION.toString(), "/user");
    private static final Path BUILD_THUMBNAIL_LOCATION = Paths.get(ROOT_LOCATION.toString(), "/build-thumb");
    private static final Path BUILD_IMAGE_LOCATION = Paths.get(ROOT_LOCATION.toString(), "/build");
    private static final Path RES_IMAGE_LOCATION = Paths.get(ROOT_LOCATION.toString(), "/res");

    private final ImageRepository imageRepo;

    private final ImageMapper imageMapper;

    private final UserClient userClient;
    private final BuildClient buildClient;
//    private final GameResClient resClient;

    public ImageService(ImageRepository imageRepo, ImageMapper imageMapper, UserClient userClient, BuildClient buildClient) {
        this.imageRepo= imageRepo;
        this.imageMapper = imageMapper;
        this.userClient = userClient;
        this.buildClient = buildClient;
//        this.resClient = resClient;
        createDir();
    }

    public ImageResponse findById(Long id){
        return imageMapper.toResponse(
                imageRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Image not found"))
        );
    }

    public ImageResponse findByOwnerId(Long ownerId){
        return null;
    }

    public Resource download(String filename){
        try{
            Path filePath = ROOT_LOCATION.resolve(filename).normalize();
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

    public Resource downloadProfilePic(Long id){
        var user = userClient.findUserById(id).getBody();
        if(user == null){
            throw new EntityNotFoundException("Build not found");
        }

        Image image = imageRepo.findImageByStoragePathAndOwnerId(USER_LOCATION.toString(), user.id())
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        try{
            Path filePath = USER_LOCATION.resolve(image.getId().toString()).normalize();
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

    public String upload(MultipartFile file){
        if(!file.getContentType().equals("image/jpeg")){
            throw new StorageException("Wrong content type");
        }
        try (InputStream input = file.getInputStream()){
            Path path = ROOT_LOCATION.resolve(file.getName());
            Files.copy(input, path, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
        return "File stored successfully";
    }


    @Transactional
    public String uploadProfilePic(MultipartFile file, String name){
        var user = userClient.findUserByName(name).getBody();
        if(user == null){
            throw new EntityNotFoundException("User not found");
        }

        var checkImage = imageRepo.findImageByStoragePathAndOwnerId(USER_LOCATION.toString(), user.id());

        if(checkImage.isPresent()){
            throw new ActionNotAllowed("Image already exists");
        }

        Image imageToSave = Image.builder()
                .filename(file.getOriginalFilename())
                .type(PROFILE_PIC)
                .size(file.getSize())
                .storagePath(USER_LOCATION.toString())
                .ownerService("user")
                .ownerId(user.id())
                .build();

        Image image = imageRepo.save(imageToSave);

        if(!file.getContentType().equals("image/jpeg")){
            throw new StorageException("Wrong content type");
        }
        try (InputStream input = file.getInputStream()){
            Path path = USER_LOCATION.resolve(image.getId().toString());
            Files.copy(input, path, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
        return "File stored successfully";
    }

    @Transactional
    public String uploadBuildThumbnail(MultipartFile file, String name){
        var build = buildClient.findByName(name).getBody();
        if(build == null){
            throw new EntityNotFoundException("Build not found");
        }

        var checkImage = imageRepo.findImageByStoragePathAndOwnerId(BUILD_THUMBNAIL_LOCATION.toString(), build.id());

        if(checkImage.isPresent()){
            throw new ActionNotAllowed("Image already exists");
        }

        Image imageToSave = Image.builder()
                .filename(file.getOriginalFilename())
                .type(BUILD_THUMBNAIL)
                .size(file.getSize())
                .storagePath(BUILD_THUMBNAIL_LOCATION.toString())
                .ownerService("build")
                .ownerId(build.id())
                .build();

        Image image = imageRepo.save(imageToSave);

        if(!file.getContentType().equals("image/jpeg")){
            throw new StorageException("Wrong content type");
        }
        try (InputStream input = file.getInputStream()){
            Path path = BUILD_THUMBNAIL_LOCATION.resolve(image.getId().toString());
            Files.copy(input, path, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
        return "File stored successfully";
    }

    @Transactional
    public String uploadBuildImages(MultipartFile file, String name){
        var build = buildClient.findByName(name).getBody();
        if(build == null){
            throw new EntityNotFoundException("Build not found");
        }

        var checkImage = imageRepo.findImageByStoragePathAndOwnerId(BUILD_IMAGE_LOCATION.toString(), build.id());

        if(checkImage.isPresent()){
            throw new ActionNotAllowed("Image already exists");
        }

        Image imageToSave = Image.builder()
                .filename(file.getOriginalFilename())
                .type(BUILD_IMAGE)
                .size(file.getSize())
                .storagePath(BUILD_IMAGE_LOCATION.toString())
                .ownerService("build")
                .ownerId(build.id())
                .build();

        Image image = imageRepo.save(imageToSave);

        if(!file.getContentType().equals("image/jpeg")){
            throw new StorageException("Wrong content type");
        }
        try (InputStream input = file.getInputStream()){
            Path path = BUILD_IMAGE_LOCATION.resolve(image.getId().toString());
            Files.copy(input, path, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
        return "File stored successfully";
    }

//    @Transactional
//    public String uploadResImage(MultipartFile file, String name){
//        var gameres = resClient.findByName(name).getBody();
//        if(gameres == null){
//            throw new EntityNotFoundException("Build not found");
//        }
//
//        var checkImage = imageRepo.findImageByStoragePathAndOwnerId(RES_IMAGE_LOCATION.toString(), gameres.id());
//
//        if(checkImage.isPresent()){
//            throw new ActionNotAllowed("Image already exists");
//        }
//
//        Image imageToSave = Image.builder()
//                .filename(file.getOriginalFilename())
//                .type(RES_IMAGE)
//                .size(file.getSize())
//                .storagePath(RES_IMAGE_LOCATION.toString())
//                .ownerService("gameres")
//                .ownerId(gameres.id())
//                .build();
//
//        Image image = imageRepo.save(imageToSave);
//
//        if(!file.getContentType().equals("image/jpeg")){
//            throw new StorageException("Wrong content type");
//        }
//        try (InputStream input = file.getInputStream()){
//            Path path = RES_IMAGE_LOCATION.resolve(image.getId().toString());
//            Files.copy(input, path, REPLACE_EXISTING);
//        } catch (IOException e) {
//            throw new StorageException(e.getMessage());
//        }
//        return "File stored successfully";
//    }

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

    @RabbitListener(queues = queueName)
    @Transactional
    public void deleteFile(DeleteEntityEvent event){
        List<Image> imgsToDelete = imageRepo.findAllByOwnerServiceIgnoreCaseAndOwnerId(event.serviceName(), event.id());

        imageRepo.deleteAll(imgsToDelete);

        try {
            switch (event.serviceName().toLowerCase()) {
                case "user" -> {
                    for (Image i : imgsToDelete) {
                        Files.deleteIfExists(USER_LOCATION.resolve(i.getId().toString()));
                    }
                }
                case "build" -> {
                    for (Image i : imgsToDelete) {
                        Files.deleteIfExists(BUILD_THUMBNAIL_LOCATION.resolve(i.getId().toString()));
                        Files.deleteIfExists(BUILD_IMAGE_LOCATION.resolve(i.getId().toString()));
                    }
                }
                case "res" -> {
                    for (Image i : imgsToDelete) {
                        Files.deleteIfExists(RES_IMAGE_LOCATION.resolve(i.getId().toString()));
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

    @Transactional
    public String testUpload(MultipartFile file, ImageType type, String name){
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
            Files.copy(input, path, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
        return "File stored successfully";
    }

    private OwnerData checkOwner(ImageType type, String name){
        OwnerData ownerData;
        switch(type){
            case PROFILE_PIC -> {
                var user = userClient.findUserByName(name).getBody();
                if(user == null){
                    throw new EntityNotFoundException("User not found");
                }
                ownerData = new OwnerData(user.id(), user.username(), "user", USER_LOCATION);
            }
//            case RES_IMAGE -> {
//                var gameres = resClient.findByName(name).getBody();
//                if(gameres == null){
//                    throw new EntityNotFoundException("Build not found");
//                }
//                ownerData = new OwnerData(gameres.id(), gameres.name(), "gameres", RES_IMAGE_LOCATION);
//            }
            case BUILD_IMAGE, BUILD_THUMBNAIL -> {
                var build = buildClient.findByName(name).getBody();
                if(build == null){
                    throw new EntityNotFoundException("Build not found");
                }
                if(type == BUILD_IMAGE){
                    ownerData = new OwnerData(build.id(), build.name(), "build", BUILD_IMAGE_LOCATION);
                }else{
                    ownerData = new OwnerData(build.id(), build.name(), "build", BUILD_THUMBNAIL_LOCATION);
                }
            }
            default -> throw new ActionNotAllowed("Owner of image couldn't be located");
        }
        return ownerData;
    }
}
