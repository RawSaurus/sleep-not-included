package com.rawsaurus.sleep_not_included.gameres.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class RangeValue implements Serializable {

    private int min;
    private int max;
    private String unit;
}
