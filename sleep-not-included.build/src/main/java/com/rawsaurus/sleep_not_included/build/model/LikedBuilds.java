package com.rawsaurus.sleep_not_included.build.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LikedBuilds {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private Long buildId;
}
