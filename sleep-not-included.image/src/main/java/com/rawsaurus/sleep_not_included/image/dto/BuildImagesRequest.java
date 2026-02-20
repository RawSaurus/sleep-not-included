package com.rawsaurus.sleep_not_included.image.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public record BuildImagesRequest(
        Map<BuildImageLayout, MultipartFile> images
) {
}
