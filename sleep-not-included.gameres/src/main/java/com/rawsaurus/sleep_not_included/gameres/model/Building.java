package com.rawsaurus.sleep_not_included.gameres.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@DiscriminatorValue("BUILDING")
public class Building extends GameRes{

    private String size;
    private int decorRadius;
    private int decorEffect;
    private double thermalConductivity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column =  @Column(name = "storage_capacity_value")),
            @AttributeOverride(name = "unit", column =  @Column(name = "storage_capacity_unit"))
    })
    private ValueUnit storageCapacity;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column =  @Column(name = "power_value")),
            @AttributeOverride(name = "unit", column =  @Column(name = "power_unit"))
    })
    private ValueUnit power;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column =  @Column(name = "heat_value")),
            @AttributeOverride(name = "unit", column =  @Column(name = "heat_unit"))
    })
    private ValueUnit heat;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column =  @Column(name = "overheat_temp_value")),
            @AttributeOverride(name = "unit", column =  @Column(name = "overheat_temp_unit"))
    })
    private ValueUnit overheatTemp;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, List<Recipe>> recipes;
}
