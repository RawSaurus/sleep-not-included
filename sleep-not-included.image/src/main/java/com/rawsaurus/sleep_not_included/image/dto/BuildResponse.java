package com.rawsaurus.sleep_not_included.image.dto;


import java.util.List;

public record BuildResponse(
        Long id,
        String name,
        String description,
        List<TagResponse> tags,
        Long creatorId,
        Integer likes) {
}
