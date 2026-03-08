package com.rawsaurus.sleep_not_included.gameres.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rawsaurus.sleep_not_included.gameres.model.ResType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        @JsonSubTypes.Type(value = ElementRequest.class,   name = "ELEMENT"),
        @JsonSubTypes.Type(value = BuildingRequest.class,  name = "BUILDING"),
        @JsonSubTypes.Type(value = CritterRequest.class,   name = "CRITTER"),
        @JsonSubTypes.Type(value = EggRequest.class,       name = "EGG"),
        @JsonSubTypes.Type(value = PlantRequest.class,     name = "PLANT"),
        @JsonSubTypes.Type(value = SeedRequest.class,      name = "SEED"),
        @JsonSubTypes.Type(value = FoodRequest.class,      name = "FOOD"),
        @JsonSubTypes.Type(value = PlanetRequest.class,    name = "PLANET"),
        @JsonSubTypes.Type(value = ArtifactRequest.class,  name = "ARTIFACT"),
        @JsonSubTypes.Type(value = CometRequest.class,     name = "COMET"),
        @JsonSubTypes.Type(value = GeyserRequest.class,    name = "GEYSER"),
        @JsonSubTypes.Type(value = MedicineRequest.class,  name = "MEDICINE"),
        @JsonSubTypes.Type(value = DiseaseRequest.class,   name = "DISEASE"),
        @JsonSubTypes.Type(value = PathogenRequest.class,  name = "PATHOGEN"),
        @JsonSubTypes.Type(value = OtherRequest.class,     name = "MISC")
})
public abstract class ResRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String imageUrl;
    @NotNull
    private ResType resType;
}