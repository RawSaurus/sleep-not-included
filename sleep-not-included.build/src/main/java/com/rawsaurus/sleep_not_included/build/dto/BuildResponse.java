package com.rawsaurus.sleep_not_included.build.dto;

import java.util.Set;

public record BuildResponse(
        Long id,
        String name,
        String description,
        Set<Long> tagsId,
        Set<Long> dlcId,
        Long creatorId
) {
}
