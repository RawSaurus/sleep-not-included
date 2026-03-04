package com.rawsaurus.sleep_not_included.build.dto;

import java.time.LocalDateTime;

public record ImageResponse(
        Long id,
        String filename,
        String type,
        Double size,
        String url,
        String storagePath,
        Long ownerId,
        LocalDateTime createdAt
) {
}
