package com.rawsaurus.sleep_not_included.build.dto;



public record BuildResLoggedIn(
        Long id,
        String name,
        String description,
        Long creatorId,
        Integer likes,
        boolean isLiked
        ) {
}
