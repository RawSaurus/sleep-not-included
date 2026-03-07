package com.rawsaurus.sleep_not_included.gameres.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Critter extends GameRes{

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "livable_range_min")),
            @AttributeOverride(name = "max", column = @Column(name = "livable_range_max")),
            @AttributeOverride(name = "unit", column = @Column(name = "livable_range_unit"))
    })
    private RangeValue livableRange;
    private int decorRadius;
    private int decorEffect;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "calories_needed_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "calories_needed_unit")),
    })
    private ValueUnit caloriesNeeded;
    private int hp;
    private int spaceRequired;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "lay_egg_every_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "lay_egg_every_unit")),
    })
    private ValueUnit layEggEvery;
    private Long dropsOnDeath;
    private boolean isWild;
    private boolean isGlum;
    //lifeCycle
    //breedingChance
    //Diet

}
