package com.rawsaurus.sleep_not_included.comment.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String body,
        String userId,
        String username,
        Integer likes,
        LocalDateTime createdAt,
        Integer numOfResponses,
        Boolean isLiked
) {
}
