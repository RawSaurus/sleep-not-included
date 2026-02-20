package com.rawsaurus.sleep_not_included.user.dto;

public record DeleteEntityEvent(
        String serviceName,
        Long id
) {
}
