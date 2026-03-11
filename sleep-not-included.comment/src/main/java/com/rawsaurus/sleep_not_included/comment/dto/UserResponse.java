package com.rawsaurus.sleep_not_included.comment.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String keycloakId,
        String username,
        String email,
        UserRole role,
        String profilePicUrl,
        LocalDateTime createdAt
){
}
