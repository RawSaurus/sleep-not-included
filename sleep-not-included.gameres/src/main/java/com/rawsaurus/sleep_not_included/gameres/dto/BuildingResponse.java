package com.rawsaurus.sleep_not_included.gameres.dto;

import com.rawsaurus.sleep_not_included.gameres.model.Recipe;
import com.rawsaurus.sleep_not_included.gameres.model.ValueUnit;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
public class BuildingResponse extends ResResponse{
    private String size;
    private int decorRadius;
    private int decorEffect;
    private double thermalConductivity;
    private ValueUnitDto storageCapacity;
    private ValueUnitDto power;
    private ValueUnitDto heat;
    private ValueUnitDto overheatTemp;
    private Map<String, List<RecipeDto>> recipes;
}
