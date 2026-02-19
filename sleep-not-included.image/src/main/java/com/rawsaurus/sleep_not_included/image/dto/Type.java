package com.rawsaurus.sleep_not_included.image.dto;

import lombok.Getter;

@Getter
public enum Type {
    DLC("dlc"),
    BUILD("build");

    private final String name;

    Type(String name){
        this.name = name;
    }
}
