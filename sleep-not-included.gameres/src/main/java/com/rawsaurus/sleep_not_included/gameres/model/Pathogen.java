package com.rawsaurus.sleep_not_included.gameres.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Pathogen extends GameRes {


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "temperature_min")),
            @AttributeOverride(name = "max", column = @Column(name = "temperature_max")),
            @AttributeOverride(name = "unit", column = @Column(name = "temperature_unit"))
    })
    private RangeValue temperature;
    //change to entity
    private Long canCause;
    //change to entity
    private List<Long> droppedBy;
    //change to entity
    private List<Long> disinfectedBy;
}
