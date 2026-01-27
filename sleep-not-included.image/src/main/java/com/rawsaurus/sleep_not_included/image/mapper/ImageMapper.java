package com.rawsaurus.sleep_not_included.image.mapper;

import com.rawsaurus.sleep_not_included.image.dto.ImageRequest;
import com.rawsaurus.sleep_not_included.image.dto.ImageResponse;
import com.rawsaurus.sleep_not_included.image.model.Image;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageResponse toResponse(Image image);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Image toEntity(ImageRequest request);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateToEntity(ImageRequest request, @MappingTarget Image image);
}
