package com.rawsaurus.sleep_not_included.user.dto;

import com.rawsaurus.sleep_not_included.user.model.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        UserRole role,
        LocalDateTime createdAt
) {
}
