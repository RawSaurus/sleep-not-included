package com.rawsaurus.sleep_not_included.gameres.dto;

import java.util.List;

public record RecipeDto(
        List<String> in,
//        String in,
        String middle,
//        String out
        List<String> out

) {
}
