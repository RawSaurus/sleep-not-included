package com.rawsaurus.sleep_not_included.gameres.mapper;

import com.rawsaurus.sleep_not_included.gameres.dto.ResSimpleRequest;
import com.rawsaurus.sleep_not_included.gameres.dto.ResSimpleResponse;
import com.rawsaurus.sleep_not_included.gameres.model.GameRes;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface GameResMapper {

    ResSimpleResponse toResponse(GameRes user);

    GameRes toEntity(ResSimpleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateToEntity(ResSimpleRequest request, @MappingTarget GameRes gameRes);
}
