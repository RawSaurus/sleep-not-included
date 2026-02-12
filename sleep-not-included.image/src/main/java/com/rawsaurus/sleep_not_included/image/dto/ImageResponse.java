package com.rawsaurus.sleep_not_included.image.dto;

import java.time.LocalDateTime;

public record ImageResponse(
        Long id,
        String filename,
        String type,
        Double size,
        String storagePath,
        Long ownerId,
        LocalDateTime createdAt
) {
}
