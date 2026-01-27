package com.rawsaurus.sleep_not_included.tag.dto;

import com.rawsaurus.sleep_not_included.tag.model.Type;

public record TagResponse(
        Long id,
        String name,
        Type type
) {
}
