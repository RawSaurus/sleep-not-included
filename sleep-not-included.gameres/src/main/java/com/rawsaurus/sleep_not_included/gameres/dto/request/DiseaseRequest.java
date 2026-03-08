package com.rawsaurus.sleep_not_included.gameres.dto.request;

import com.rawsaurus.sleep_not_included.gameres.dto.ValueUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class DiseaseRequest extends ResRequest {
    private ValueUnitDto duration;
    private String severity;
    private String type;
    private String gotBy;
    private Long curedBy;
    private List<Long> causedBy;
}