package com.rawsaurus.sleep_not_included.gameres.dto;

import com.rawsaurus.sleep_not_included.gameres.model.ResType;

public record ResSimpleResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        ResType resType
        ) {
}
