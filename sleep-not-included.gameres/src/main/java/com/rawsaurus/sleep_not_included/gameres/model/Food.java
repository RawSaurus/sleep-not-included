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
public class Food extends GameRes {

    private int quality;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "calories_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "calories_unit"))
    })
    private ValueUnit calories;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "spoil_time_value")),
            @AttributeOverride(name = "unit", column = @Column(name = "spoil_time_unit"))
    })
    private ValueUnit spoilTime;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, List<Recipe>> recipes;

    //duplicant diet
}
