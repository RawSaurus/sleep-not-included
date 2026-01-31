package com.rawsaurus.sleep_not_included.comment.dto;

import java.util.Set;

public record BuildResponse(
        Long id,
        String name,
        String description,
        Set<TagResponse> tagsId,
        Long creatorId
) {
}
