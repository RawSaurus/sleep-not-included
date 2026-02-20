package com.rawsaurus.sleep_not_included.image.dto;

public record DeleteEntityEvent(
        String serviceName,
        Long id
) {
}
