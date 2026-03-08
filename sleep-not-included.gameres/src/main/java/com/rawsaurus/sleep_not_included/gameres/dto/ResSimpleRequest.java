package com.rawsaurus.sleep_not_included.gameres.dto;

import com.rawsaurus.sleep_not_included.gameres.model.ResType;

public record ResSimpleRequest(
        String name,
        String description,
        String imageUrl,
        ResType resType
) {
}
