package com.rawsaurus.sleep_not_included.gameres.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
@Builder
public class RangeValue implements Serializable {

    private int min;
    private int max;
    private String unit;
}
