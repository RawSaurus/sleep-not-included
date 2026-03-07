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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Medicine extends GameRes {

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "duration_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "duration_unit"))
    })
    private ValueUnit duration;
    private double radiationRecovery;
    private double germResistance;
    //change to entity
    private Long disease;
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Recipe recipe;
}
