package com.rawsaurus.sleep_not_included.user.dto;

public record UpdateImageUrlEvent(
        Long userId,
        String imageUrl
) {
}
