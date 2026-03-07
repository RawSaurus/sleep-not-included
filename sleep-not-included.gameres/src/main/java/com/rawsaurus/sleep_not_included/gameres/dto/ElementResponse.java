package com.rawsaurus.sleep_not_included.gameres.dto;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rawsaurus.sleep_not_included.gameres.model.Recipe;
import com.rawsaurus.sleep_not_included.gameres.model.State;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ElementResponse extends ResResponse{

    private double thermalConductivity;
    private double specificHeatCapacity;
    private double molarMass;
    private int hardness;
    private int lightAbsorptionFactor;
    private State state;
    private Map<String, Object> stateTransition;
    private Map<String, List<Recipe>> recipes;
}
