package com.rawsaurus.sleep_not_included.gameres.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ArtifactRequest extends ResRequest {
    private int tier;
    private int decorRadius;
    private int decorEffect;
}