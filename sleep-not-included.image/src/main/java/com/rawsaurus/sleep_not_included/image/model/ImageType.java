package com.rawsaurus.sleep_not_included.image.model;

import lombok.Getter;

@Getter
public enum ImageType {

    PROFILE_PIC("profile_pic"),
    BUILD_THUMBNAIL("build_thumbnail"),
    BUILD_IMAGE("build_image"),
    RES_IMAGE("res_image");

    private final String name;

    ImageType(String name){
        this.name = name;
    }
}
