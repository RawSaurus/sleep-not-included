package com.rawsaurus.sleep_not_included.image.dto;

import java.nio.file.Path;

public record OwnerData(
        Long id,
        String name,
        String ownerService,
        Path location
) {
}
