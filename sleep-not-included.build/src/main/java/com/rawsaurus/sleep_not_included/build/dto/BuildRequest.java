package com.rawsaurus.sleep_not_included.build.dto;

import java.util.List;
import java.util.Set;

public record BuildRequest(
        String name,
        String description,
        List<Long> tagId,
        Long creatorId) {
}
