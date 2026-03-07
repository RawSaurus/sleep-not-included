package com.rawsaurus.sleep_not_included.gameres.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
@Setter
public class ValueUnit implements Serializable {
    private int value;
    private String unit;
}
