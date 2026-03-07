package com.rawsaurus.sleep_not_included.gameres.dto;

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
        @JsonSubTypes.Type(value = ElementResponse.class, name = "ELEMENT"),
        @JsonSubTypes.Type(value = BuildingResponse.class, name = "BUILDING")
})
public abstract class ResResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private ResType resType;
}
