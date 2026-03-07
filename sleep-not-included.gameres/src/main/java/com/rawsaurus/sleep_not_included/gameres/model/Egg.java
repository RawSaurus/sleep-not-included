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
public class Egg extends GameRes{

    //lifeCycle
    private Long laidBy;
    //increasingBreedingChance
    //recipe, List not needed ?
}
