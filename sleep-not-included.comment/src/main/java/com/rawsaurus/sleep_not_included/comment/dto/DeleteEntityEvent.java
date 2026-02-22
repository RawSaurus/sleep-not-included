package com.rawsaurus.sleep_not_included.comment.dto;

public record DeleteEntityEvent(
        String serviceName,
        Long id
) {
}
