package com.rawsaurus.sleep_not_included.build.dto;

import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


public record BuildDetailResponse(
    Long id,
    String name,
    String description,
    int likes,
    LocalDateTime createdAt,
    String creatorName,
    String thumbnailUrl,
    List<String> imageUrls,
    List<TagResponse> tags,
    Boolean isLiked
){
}