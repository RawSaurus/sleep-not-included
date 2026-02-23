package com.rawsaurus.sleep_not_included.tag.dto;

public record DeleteEntityEvent(
        String serviceName,
        Long id
) {
}
