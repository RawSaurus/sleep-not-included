package com.rawsaurus.sleep_not_included.gameres.dto.request;

import com.rawsaurus.sleep_not_included.gameres.dto.RangeValueDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class CometRequest extends ResRequest {
    private Long drops;
    private RangeValueDto composition;
    private double temperature;
}