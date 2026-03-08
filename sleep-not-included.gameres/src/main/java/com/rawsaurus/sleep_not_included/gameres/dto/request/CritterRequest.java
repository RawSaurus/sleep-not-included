package com.rawsaurus.sleep_not_included.gameres.dto.request;

import com.rawsaurus.sleep_not_included.gameres.dto.RangeValueDto;
import com.rawsaurus.sleep_not_included.gameres.dto.ValueUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class CritterRequest extends ResRequest {
    private RangeValueDto livableRange;
    private int decorRadius;
    private int decorEffect;
    private ValueUnitDto caloriesNeeded;
    private int hp;
    private int spaceRequired;
    private ValueUnitDto layEggEvery;
    private Long dropsOnDeath;
    private boolean isWild;
    private boolean isGlum;
}