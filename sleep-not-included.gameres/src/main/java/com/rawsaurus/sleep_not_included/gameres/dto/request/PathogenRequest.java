package com.rawsaurus.sleep_not_included.gameres.dto.request;

import com.rawsaurus.sleep_not_included.gameres.dto.RangeValueDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class PathogenRequest extends ResRequest {
    private RangeValueDto temperature;
    private Long canCause;
    private List<Long> droppedBy;
    private List<Long> disinfectedBy;
}