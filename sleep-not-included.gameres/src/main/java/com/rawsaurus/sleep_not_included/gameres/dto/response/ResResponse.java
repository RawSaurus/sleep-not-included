package com.rawsaurus.sleep_not_included.gameres.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rawsaurus.sleep_not_included.gameres.model.ResType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "resType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ElementResponse.class,   name = "ELEMENT"),
        @JsonSubTypes.Type(value = BuildingResponse.class,  name = "BUILDING"),
        @JsonSubTypes.Type(value = CritterResponse.class,   name = "CRITTER"),
        @JsonSubTypes.Type(value = EggResponse.class,       name = "EGG"),
        @JsonSubTypes.Type(value = PlantResponse.class,     name = "PLANT"),
        @JsonSubTypes.Type(value = SeedResponse.class,      name = "SEED"),
        @JsonSubTypes.Type(value = FoodResponse.class,      name = "FOOD"),
        @JsonSubTypes.Type(value = PlanetResponse.class,    name = "PLANET"),
        @JsonSubTypes.Type(value = ArtifactResponse.class,  name = "ARTIFACT"),
        @JsonSubTypes.Type(value = CometResponse.class,     name = "COMET"),
        @JsonSubTypes.Type(value = GeyserResponse.class,    name = "GEYSER"),
        @JsonSubTypes.Type(value = MedicineResponse.class,  name = "MEDICINE"),
        @JsonSubTypes.Type(value = DiseaseResponse.class,   name = "DISEASE"),
        @JsonSubTypes.Type(value = PathogenResponse.class,  name = "PATHOGEN"),
        @JsonSubTypes.Type(value = OtherResponse.class,     name = "MISC")
})
public abstract class ResResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private ResType resType;
}