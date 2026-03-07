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
public class Geyser extends GameRes{

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "max_pressure_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "max_pressure_unit")),
    })
    private ValueUnit maxPressure;
    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "avg_emission_rate_min")),
            @AttributeOverride(name = "max", column = @Column(name = "avg_emission_rate_max")),
            @AttributeOverride(name = "unit", column = @Column(name = "avg_emission_rate_unit"))
    })
    private RangeValue avgEmissionRate;
    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "total_eruption_period_min")),
            @AttributeOverride(name = "max", column = @Column(name = "total_eruption_period_max")),
            @AttributeOverride(name = "unit", column = @Column(name = "total_eruption_period_unit"))
    })
    private RangeValue totalEruptionPeriod;
    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "active_of_eruption_period_min")),
            @AttributeOverride(name = "max", column = @Column(name = "active_of_eruption_period_max")),
            @AttributeOverride(name = "unit", column = @Column(name = "active_of_eruption_period_unit"))
    })
    private RangeValue activeOfEruptionPeriod;
    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "total_period_min")),
            @AttributeOverride(name = "max", column = @Column(name = "total_period_max")),
            @AttributeOverride(name = "unit", column = @Column(name = "total_period_unit"))
    })
    private RangeValue totalPeriod;
    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "active_period_min")),
            @AttributeOverride(name = "max", column = @Column(name = "active_period_max")),
            @AttributeOverride(name = "unit", column = @Column(name = "active_period_unit"))
    })
    private RangeValue activePeriod;
}
