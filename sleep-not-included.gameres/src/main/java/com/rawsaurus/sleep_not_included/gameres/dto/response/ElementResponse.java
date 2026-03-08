package com.rawsaurus.sleep_not_included.gameres.dto.response;


import com.rawsaurus.sleep_not_included.gameres.model.State;
import com.rawsaurus.sleep_not_included.gameres.dto.RecipeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ElementResponse extends ResResponse {
    private double thermalConductivity;
    private double specificHeatCapacity;
    private double molarMass;
    private int hardness;
    private int lightAbsorptionFactor;
    private State state;
    private Map<String, Object> stateTransition;
    private Map<String, List<RecipeDto>> recipes;
}
