package com.rawsaurus.sleep_not_included.image.dto;

public record UpdateImageUrlEvent(
        Long userId,
        String imageUrl
) {
}
