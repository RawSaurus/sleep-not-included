package com.rawsaurus.sleep_not_included.gameres.mapper;

import com.rawsaurus.sleep_not_included.gameres.dto.*;
import com.rawsaurus.sleep_not_included.gameres.handler.ActionNotAllowed;
import com.rawsaurus.sleep_not_included.gameres.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ResMapper {

    public ResResponse toResponse(GameRes res){
        return switch (res.getResType()){
            case ELEMENT -> toElementResponse((Element) res);
            case BUILDING -> toBuildingResponse((Building) res);
            default -> throw new ActionNotAllowed("Invalid type: " + res.getResType());
        };
    }

    private ElementResponse toElementResponse(Element e){
        return ElementResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .imageUrl(e.getImageUrl())
                .resType(e.getResType())
                .thermalConductivity(e.getThermalConductivity())
                .specificHeatCapacity(e.getSpecificHeatCapacity())
                .molarMass(e.getMolarMass())
                .hardness(e.getHardness())
                .lightAbsorptionFactor(e.getLightAbsorptionFactor())
                .state(e.getState())
                .stateTransition(e.getStateTransition())
                .recipes(e.getRecipes())
                .build();
    }

    private BuildingResponse toBuildingResponse(Building b){
        return BuildingResponse.builder()
                .id(b.getId())
                .name(b.getName())
                .description(b.getDescription())
                .imageUrl(b.getImageUrl())
                .resType(b.getResType())
                .size(b.getSize())
                .decorRadius(b.getDecorRadius())
                .decorEffect(b.getDecorEffect())
                .thermalConductivity(b.getThermalConductivity())
                .storageCapacity(toValueUnitDto(b.getStorageCapacity()))
                .power(toValueUnitDto(b.getPower()))
                .heat(toValueUnitDto(b.getHeat()))
                .overheatTemp(toValueUnitDto(b.getOverheatTemp()))
                .recipes(toMapRecipeDto(b.getRecipes()))
                .build();
    }

    private ValueUnitDto toValueUnitDto(ValueUnit valueUnit){
        if(valueUnit == null) return null;
        return new ValueUnitDto(
                valueUnit.getValue(),
                valueUnit.getUnit()
        );
    }

    private Map<String, List<RecipeDto>> toMapRecipeDto(Map<String, List<Recipe>> recipes){
        Map<String, List<RecipeDto>> dtos = new HashMap<>();
        for(String s : recipes.keySet()){
            dtos.put(s, recipes.get(s).stream().map(this::toRecipeDto).toList());
        }
        return dtos;
    }

    private RecipeDto toRecipeDto(Recipe recipe){
        if(recipe == null) return null;
        return new RecipeDto(
                recipe.getInp(),
                recipe.getMiddle(),
                recipe.getOut()
        );
    }
}
