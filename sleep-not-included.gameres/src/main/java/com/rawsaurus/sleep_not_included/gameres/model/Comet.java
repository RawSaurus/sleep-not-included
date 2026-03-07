package com.rawsaurus.sleep_not_included.gameres.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Comet extends GameRes {

    //change to entity?
    private Long drops;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "composition_min")),
            @AttributeOverride(name = "unit", column = @Column(name = "composition_max")),
            @AttributeOverride(name = "unit", column = @Column(name = "composition_unit"))
    })
    private RangeValue composition;
    private double temperature;
}
