package com.rawsaurus.sleep_not_included.tag;

import com.rawsaurus.sleep_not_included.tag.dto.TagRequest;
import com.rawsaurus.sleep_not_included.tag.dto.TagResponse;
import com.rawsaurus.sleep_not_included.tag.model.Tag;
import com.rawsaurus.sleep_not_included.tag.model.Type;

public final class TagTestFixtures {

    private TagTestFixtures(){

    }

    public static Tag aDlcTag(){
        return Tag.builder()
                .name("Spaced Out")
                .type(Type.DLC)
                .build();
    }

    public static Tag aBuildTag(){
        return Tag.builder()
                .name("SPOM")
                .type(Type.BUILD)
                .build();
    }

    public static Tag aDlcTagWithId(Long id){
        return Tag.builder()
                .id(id)
                .name("Spaced Out")
                .type(Type.DLC)
                .build();
    }

    public static Tag aBuildTagWithId(Long id){
        return Tag.builder()
                .id(id)
                .name("SPOM")
                .type(Type.BUILD)
                .build();
    }

    public static TagRequest aValidTagRequest(){
        return new TagRequest("Spaced Out", Type.DLC);
    }

    public static TagRequest aBuildTagRequest(){
        return new TagRequest("SPOM", Type.BUILD);
    }

    public static TagRequest aRequestWithBlankName(){
        return new TagRequest("", Type.DLC);
    }

    public static TagRequest aRequestWithTooLongName(){
        return new TagRequest("a".repeat(31), Type.DLC);
    }

    public static TagResponse aDlcTagResponse(Long id){
        return new TagResponse(id, "Spaced Out", Type.DLC);
    }

    public static TagResponse aBuildTagResponse(Long id){
        return new TagResponse(id, "SPOM", Type.BUILD);
    }
}
