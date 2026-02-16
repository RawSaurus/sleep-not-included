package com.rawsaurus.sleep_not_included.game_res.model;

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
