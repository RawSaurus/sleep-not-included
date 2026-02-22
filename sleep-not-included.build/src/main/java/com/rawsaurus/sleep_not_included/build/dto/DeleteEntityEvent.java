package com.rawsaurus.sleep_not_included.build.dto;

public record DeleteEntityEvent(
        String serviceName,
        Long id
) {
}
