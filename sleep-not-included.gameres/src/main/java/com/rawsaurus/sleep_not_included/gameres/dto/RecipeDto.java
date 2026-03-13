package com.rawsaurus.sleep_not_included.gameres.dto;

import java.util.List;

public record RecipeDto(
        List<String> in,
        String middle,
        List<String> out

) {
}
