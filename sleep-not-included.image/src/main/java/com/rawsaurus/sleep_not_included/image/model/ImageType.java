package com.rawsaurus.sleep_not_included.image.model;

import lombok.Getter;

@Getter
public enum ImageType {

    PROFILE_PIC("profile pic"),
    BUILD_THUMBNAIL("build thumbnail"),
    BUILD_IMAGE("build image"),
    RES_IMAGE("res image");

    private final String name;

    ImageType(String name){
        this.name = name;
    }
}
