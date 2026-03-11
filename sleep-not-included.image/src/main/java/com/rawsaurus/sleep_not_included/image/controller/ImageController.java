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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/image", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImageController {

    private final ImageService imageService;
    @Value("${sni.message}")
    private String message;
    @Value("${server.tomcat.max-swallow-size}")
    private String maxSwallowSize;
    @Value("${spring.servlet.multipart.max-file-size}")
    private String servletMaxFileSize;
    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    @GetMapping("/test")
    public String test(){
        return "Max Sallow Size: " + maxSwallowSize +"\n"
                + "Servlet Max File Size: " + servletMaxFileSize + "\n"
                + "Max Request Size: " + maxRequestSize;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(imageService.findById(id));
    }

    @GetMapping("/owner/{ownerService}/{ownerId}")
    public ResponseEntity<List<ImageResponse>> findByOwner(@PathVariable String ownerService, @PathVariable Long ownerId){
        return ResponseEntity.ok(imageService.findByOwner(ownerService, ownerId));
    }

    @GetMapping("/owner/{ownerService}/{ownerId}/url")
    public ResponseEntity<List<String>> findUrlByOwner(@PathVariable String ownerService, @PathVariable Long ownerId){
        return ResponseEntity.ok(imageService.findUrlByOwner(ownerService, ownerId));
    }

    @GetMapping("/owner/{ownerService}/batch")
    public ResponseEntity<List<ImageResponse>> findAllByOwnerIds(@PathVariable String ownerService, @RequestParam List<Long> ownerIds){
        return ResponseEntity.ok(imageService.findAllByOwnerIds(ownerService, ownerIds));
    }

    @GetMapping("/owner/{ownerService}/batch/url")
    public ResponseEntity<List<String>> findAllUrlByOwnerIds(@PathVariable String ownerService, @RequestParam List<Long> ownerIds){
        return ResponseEntity.ok(imageService.findAllUrlByOwnerIds(ownerService, ownerIds));
    }
//
//    @GetMapping(value = "/download/{name}", produces = MediaType.IMAGE_JPEG_VALUE)
//    public ResponseEntity<Resource> downloadImage(@RequestParam ImageType type, @PathVariable String name){
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(imageService.downloadImage(type, name));
//    }
//
//    @GetMapping(value = "/download/build-images/{name}", produces = MediaType.MULTIPART_MIXED_VALUE)
//    public ResponseEntity<MultiValueMap<String, Object>> downloadBuildImages(@PathVariable String name){
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.MULTIPART_MIXED).body(imageService.downloadBuildImages(name));
//    }

    @PostMapping(value = "/upload/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> uploadImage(
            @RequestParam MultipartFile file,
            @RequestParam ImageType type,
            @PathVariable String name
    ){
        return ResponseEntity.ok(imageService.uploadImage(file, type, name));
    }

    @PostMapping(value = "/upload/build-images/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> uploadBuildImages(
            @RequestParam List<MultipartFile> files,
            @PathVariable String name
    ){
        return ResponseEntity.ok(imageService.uploadBuildImages(files, name));
    }

    @PutMapping(value = "/update/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateImage(
            @RequestParam MultipartFile file,
            @RequestParam ImageType type,
            @PathVariable String name
    ){
        return ResponseEntity.ok(imageService.updateImage(file, type, name));
    }

    @DeleteMapping("/{serviceName}/{ownerId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteImage(
            @PathVariable String serviceName,
            @PathVariable Long ownerId
    ){
        return ResponseEntity.ok(imageService.deleteFile(serviceName, ownerId));
    }
}
