package com.rawsaurus.sleep_not_included.gameres.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class Recipe implements Serializable {

//    @Type(JsonType.class)
//    @Column(columnDefinition = "jsonb")
    private List<String> inp;
//    private String in;
    private String middle;
//    private String out;
//    @Type(JsonType.class)
//    @Column(columnDefinition = "jsonb")
    private List<String> out;
}
