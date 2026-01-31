package com.rawsaurus.sleep_not_included.tag.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class BuildTags {

    @Id
    @GeneratedValue
    private Long id;

    private Long buildId;
    private Long tagId;
}