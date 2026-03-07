package com.rawsaurus.sleep_not_included.gameres.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Disease extends GameRes{

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "duration_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "duration_unit"))
    })
    private ValueUnit duration;
    private String severity;
    private String type;
    private String gotBy;
    //symptoms
    //change to entity
    private Long curedBy;
    //change to entity
    private List<Long> causedBy;
}
