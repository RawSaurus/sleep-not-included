package com.rawsaurus.sleep_not_included.gameres.model;

import lombok.Getter;

@Getter
public enum State {
    SOLID("solid"),
    LIQUID("liquid"),
    GAS("gas");

    private final String name;

    State(String name){
        this.name = name;
    }
}
