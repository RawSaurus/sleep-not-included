package com.rawsaurus.sleep_not_included.gameres.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
public class Artifact extends GameRes {

    private int tier;
    private int decorRadius;
    private int decorEffect;
}
