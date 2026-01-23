package com.rawsaurus.sleep_not_included.user.dto;

public record UserRequest(
        String username,
        String email,
        String password
        ) {
}
