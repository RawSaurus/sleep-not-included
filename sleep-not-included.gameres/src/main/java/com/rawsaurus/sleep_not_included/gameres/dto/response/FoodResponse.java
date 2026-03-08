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
public class FoodResponse extends ResResponse {
    private int quality;
    private ValueUnitDto calories;
    private ValueUnitDto spoilTime;
    private Map<String, List<RecipeDto>> recipes;
}