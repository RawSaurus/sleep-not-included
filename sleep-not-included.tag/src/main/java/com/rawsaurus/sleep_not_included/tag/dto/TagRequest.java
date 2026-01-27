package com.rawsaurus.sleep_not_included.tag.dto;

import com.rawsaurus.sleep_not_included.tag.model.Type;

public record TagRequest(
        String name,
        Type type
        ) {
}
