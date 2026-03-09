package com.rawsaurus.sleep_not_included.build.dto;



public record BuildResponse(
        Long id,
        String name,
        String shortDescription,
        Long creatorId,
        Integer likes
) {
}
