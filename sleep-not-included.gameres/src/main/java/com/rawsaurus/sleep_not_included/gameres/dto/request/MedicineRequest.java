package com.rawsaurus.sleep_not_included.gameres.dto.request;

import com.rawsaurus.sleep_not_included.gameres.dto.RecipeDto;
import com.rawsaurus.sleep_not_included.gameres.dto.ValueUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class MedicineRequest extends ResRequest {
    private ValueUnitDto duration;
    private double radiationRecovery;
    private double germResistance;
    private Long disease;
    private RecipeDto recipe;
}