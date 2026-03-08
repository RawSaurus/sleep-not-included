package com.rawsaurus.sleep_not_included.gameres.dto.response;

import com.rawsaurus.sleep_not_included.gameres.dto.RangeValueDto;
import com.rawsaurus.sleep_not_included.gameres.dto.ValueUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class GeyserResponse extends ResResponse {
    private ValueUnitDto maxPressure;
    private RangeValueDto avgEmissionRate;
    private RangeValueDto totalEruptionPeriod;
    private RangeValueDto activeOfEruptionPeriod;
    private RangeValueDto totalPeriod;
    private RangeValueDto activePeriod;
}