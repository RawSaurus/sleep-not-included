package com.rawsaurus.sleep_not_included.image.controller;

import com.rawsaurus.sleep_not_included.image.dto.ImageResponse;
import com.rawsaurus.sleep_not_included.image.model.ImageType;
import com.rawsaurus.sleep_not_included.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;
    @Value("${sni.message}")
    private String message;

    @GetMapping("/test")
    public String test(){
        return message;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(imageService.findById(id));
    }

    @GetMapping("/download/{name}")
    public ResponseEntity<Resource> downloadImage(@RequestParam ImageType type, @PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(imageService.downloadImage(type, name));
    }

    @GetMapping("/download/build-images/{name}")
    public ResponseEntity<List<Resource>> downloadBuildImages(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(imageService.downloadBuildImages(name));
    }

    @PostMapping("/upload/{name}")
    public ResponseEntity<?> uploadImage(
            @RequestParam MultipartFile file,
            @RequestParam ImageType type,
            @PathVariable String name
    ){
        return ResponseEntity.ok(imageService.uploadImage(file, type, name));
    }

    @PostMapping("/upload/build-images/{name}")
    public ResponseEntity<?> uploadTest(
            @RequestParam List<MultipartFile> files,
            @PathVariable String name
    ){
        return ResponseEntity.ok(imageService.uploadBuildImages(files, name));
    }

    @PutMapping("/update/{name}")
    public ResponseEntity<?> updateImage(
            @RequestParam MultipartFile file,
            @RequestParam ImageType type,
            @PathVariable String name
    ){
        return ResponseEntity.ok(imageService.updateImage(file, type, name));
    }

    @DeleteMapping("/{serviceName}/{ownerId}")
    public ResponseEntity<?> deleteImage(
            @PathVariable String serviceName,
            @PathVariable Long ownerId
    ){
        return ResponseEntity.ok(imageService.deleteFile(serviceName, ownerId));
    }
}
