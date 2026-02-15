package com.rawsaurus.sleep_not_included.image.controller;

import com.rawsaurus.sleep_not_included.image.dto.ImageResponse;
import com.rawsaurus.sleep_not_included.image.service.ImageService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{id}")
    public ResponseEntity<ImageResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(imageService.findById(id));
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<ImageResponse> findByOwnerId(@PathVariable Long ownerId){
        return ResponseEntity.ok(imageService.findByOwnerId(ownerId));
    }

}
