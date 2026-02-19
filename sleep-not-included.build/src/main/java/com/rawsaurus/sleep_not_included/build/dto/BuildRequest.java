package com.rawsaurus.sleep_not_included.build.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record BuildRequest(
        @NotBlank(message = "Name can't be blank")
        @Size(max = 100, message = "Name can't be longer than 100 characters")
        String name,
        @NotBlank(message = "Description can't be blank")
        @Size(max = 1000, message = "Description can't be longer than 1000 characters")
        String description,
        List<Long> tagId,
        @NotNull
        Long creatorId
) {
}
