package com.rawsaurus.sleep_not_included.comment.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String body,
        Integer likes,
        LocalDateTime createdAt,
        Integer numOfResponses
) {
}
