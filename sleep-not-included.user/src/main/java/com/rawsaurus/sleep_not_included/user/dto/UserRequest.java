package com.rawsaurus.sleep_not_included.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min=3, max=30, message = "Username must be between 3-30 characters")
        String username,
        @Email
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Size(min=8, max=30, message = "Password must be between 8-30 characters")
        String password
        ) {
}
