package com.rawsaurus.sleep_not_included.image.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String profilePicUrl,
        LocalDateTime createdAt) {
}
