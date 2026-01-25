package com.rawsaurus.sleep_not_included.build.mapper;

import com.rawsaurus.sleep_not_included.build.dto.BuildRequest;
import com.rawsaurus.sleep_not_included.build.dto.BuildResponse;
import com.rawsaurus.sleep_not_included.build.model.Build;
import org.mapstruct.*;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface BuildMapper {

    BuildResponse toResponse(Build build);

    Build toEntity(BuildRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateToEntity(BuildRequest request, @MappingTarget Build build);
}
