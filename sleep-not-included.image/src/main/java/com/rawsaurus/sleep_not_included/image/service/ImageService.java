package com.rawsaurus.sleep_not_included.image.service;

import com.rawsaurus.sleep_not_included.image.dto.ImageResponse;
import com.rawsaurus.sleep_not_included.image.handler.StorageException;
import com.rawsaurus.sleep_not_included.image.mapper.ImageMapper;
import com.rawsaurus.sleep_not_included.image.model.Image;
import com.rawsaurus.sleep_not_included.image.repo.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class ImageService {

    private static final int FILE_CODE_LENGTH = 8;
    private final Path rootLocation = Paths.get("./sleep-not-included.image/Files-Upload");

    private final ImageRepository imageRepo;
    private final ImageMapper imageMapper;

    public ImageService(ImageRepository imageRepo, ImageMapper imageMapper) {
        this.imageRepo= imageRepo;
        this.imageMapper = imageMapper;
        createDir();
    }

    public ImageResponse findById(Long id){
        return null;
    }

    public ImageResponse findByOwnerId(Long ownerId){
        return null;
    }

    public Resource download(String filename){
        try{
            Path filePath = rootLocation.resolve(filename).normalize();
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
            Path path = rootLocation.resolve(file.getName());
            Files.copy(input, path, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }
        return "File stored successfully";
    }



    private void createDir(){
        try {
            if(!Files.exists(rootLocation)) {
                Files.createDirectory(rootLocation);
            }
        } catch (IOException e) {
            throw new StorageException("Could not create directory " + rootLocation);
        }
    }

    private String randomAlphaNumeric(){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i<FILE_CODE_LENGTH;){
            int next = random.nextInt(48, 126);
            if((next >= 48 && next <= 57) || (next >= 65 && next <= 90) || (next >= 97 && next <= 122)) {
                sb.append((char)next);
                i++;
            }
        }
        return sb.toString();
    }
}
