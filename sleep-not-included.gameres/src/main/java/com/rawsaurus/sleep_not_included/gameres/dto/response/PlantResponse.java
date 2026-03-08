package com.rawsaurus.sleep_not_included.gameres.dto.response;

import com.rawsaurus.sleep_not_included.gameres.dto.RangeValueDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class PlantResponse extends ResResponse {
    private RangeValueDto airPressure;
    private RangeValueDto temp;
    private int baseDecor;
}