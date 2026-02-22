package com.rawsaurus.sleep_not_included.build.dto;



public record BuildResponse(
        Long id,
        String name,
        String description,
        Long creatorId,
        Integer likes
) {
}
