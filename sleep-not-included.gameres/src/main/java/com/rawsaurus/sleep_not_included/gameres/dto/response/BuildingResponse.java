package com.rawsaurus.sleep_not_included.gameres.dto.response;

import com.rawsaurus.sleep_not_included.gameres.dto.RecipeDto;
import com.rawsaurus.sleep_not_included.gameres.dto.ValueUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class BuildingResponse extends ResResponse {
    private String size;
    private int decorRadius;
    private int decorEffect;
    private double thermalConductivity;
    private ValueUnitDto storageCapacity;
    private ValueUnitDto power;
    private ValueUnitDto heat;
    private ValueUnitDto overheatTemp;
    private Map<String, List<RecipeDto>> recipes;
}
