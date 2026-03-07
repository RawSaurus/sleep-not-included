package com.rawsaurus.sleep_not_included.gameres.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@DiscriminatorValue("ELEMENT")
public class Element extends GameRes {

    private double thermalConductivity;
    private double specificHeatCapacity;
    private double molarMass;
    private int hardness;
    private int lightAbsorptionFactor;

    @Enumerated(EnumType.STRING)
    private State state;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> stateTransition;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, List<Recipe>> recipes;
//    private List<Recipe> consumes;
//
//    private List<Recipe> produces;
}
