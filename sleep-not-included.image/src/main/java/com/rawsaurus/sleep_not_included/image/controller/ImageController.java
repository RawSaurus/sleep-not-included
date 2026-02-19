package com.rawsaurus.sleep_not_included.image.controller;

import com.rawsaurus.sleep_not_included.image.dto.ImageResponse;
import com.rawsaurus.sleep_not_included.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/{ownerId}")
    public ResponseEntity<ImageResponse> findByOwnerId(@PathVariable Long ownerId){
        return ResponseEntity.ok(imageService.findByOwnerId(ownerId));
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename){
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(imageService.download(filename));
    }

    @GetMapping("/download/profile/{id}")
    public ResponseEntity<Resource> downloadProfilePic(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(imageService.downloadProfilePic(id));
    }

    @GetMapping("/download/build-thumbnail/{id}")
    public ResponseEntity<Resource> downloadBuildThumbnail(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(imageService.downloadBuildThumbnail(id));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file){
        return ResponseEntity.ok(imageService.upload(file));
    }

    @PostMapping("/upload/profile/{name}")
    public ResponseEntity<?> uploadProfilePic(@RequestParam MultipartFile file, @PathVariable String name){
        return ResponseEntity.ok(imageService.uploadProfilePic(file, name));
    }

    @PostMapping("/upload/build-thumbnail/{name}")
    public ResponseEntity<?> uploadBuildThumbnail(@RequestParam MultipartFile file, @PathVariable String name){
        return ResponseEntity.ok(imageService.uploadBuildThumbnail(file, name));
    }
}
