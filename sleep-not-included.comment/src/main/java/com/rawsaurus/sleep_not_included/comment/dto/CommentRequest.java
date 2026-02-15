package com.rawsaurus.sleep_not_included.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank(message = "Comment can't be blank")
        @Size(max = 1000, message = "Max size is 1000 characters")
        String body
) {
}
