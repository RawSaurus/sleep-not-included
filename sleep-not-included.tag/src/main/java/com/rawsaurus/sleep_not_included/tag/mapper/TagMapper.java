package com.rawsaurus.sleep_not_included.tag.mapper;

import com.rawsaurus.sleep_not_included.tag.dto.TagRequest;
import com.rawsaurus.sleep_not_included.tag.dto.TagResponse;
import com.rawsaurus.sleep_not_included.tag.model.Tag;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface TagMapper {

    Tag toEntity(TagRequest request);

    TagResponse toResponse(Tag tag);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateToEntity(TagRequest request, @MappingTarget Tag tag);
}
