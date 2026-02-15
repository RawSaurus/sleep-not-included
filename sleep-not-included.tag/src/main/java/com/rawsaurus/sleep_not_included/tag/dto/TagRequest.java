package com.rawsaurus.sleep_not_included.tag.dto;

import com.rawsaurus.sleep_not_included.tag.model.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequest(
        @NotBlank(message = "Can not be blank")
        @Size(max = 30, message = "Max size is 30 characters")
        String name,
        Type type
        ) {
}
