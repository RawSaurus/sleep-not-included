package com.rawsaurus.sleep_not_included.gameres.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class EggResponse extends ResResponse {
    private Long laidBy;
}
