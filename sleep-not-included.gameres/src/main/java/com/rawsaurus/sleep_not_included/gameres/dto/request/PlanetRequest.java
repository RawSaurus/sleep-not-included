package com.rawsaurus.sleep_not_included.gameres.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class PlanetRequest extends ResRequest {
    private List<Long> composition;
    private List<Long> resources;
}