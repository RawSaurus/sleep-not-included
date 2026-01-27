package com.rawsaurus.sleep_not_included.comment.dto;

public record CommentRequest(
        String body,
        Integer likes
) {
}
