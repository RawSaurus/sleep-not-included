package com.rawsaurus.sleep_not_included.gameres.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Plant extends GameRes{

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "air_pressure_min")),
            @AttributeOverride(name = "max", column = @Column(name = "air_pressure_max")),
            @AttributeOverride(name = "unit", column = @Column(name = "air_pressure_unit"))
    })
    private RangeValue airPressure;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "temp_min")),
            @AttributeOverride(name = "max", column = @Column(name = "temp_max")),
            @AttributeOverride(name = "unit", column = @Column(name = "temp_unit"))
    })
    private RangeValue temp;
    private int baseDecor;
    //growth
    //atmosphere
    //require
}
