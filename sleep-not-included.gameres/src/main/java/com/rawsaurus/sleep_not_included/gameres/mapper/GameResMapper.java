package com.rawsaurus.sleep_not_included.gameres.mapper;

import com.rawsaurus.sleep_not_included.gameres.dto.GameResRequest;
import com.rawsaurus.sleep_not_included.gameres.dto.GameResResponse;
import com.rawsaurus.sleep_not_included.gameres.model.GameRes;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface GameResMapper {

    GameResResponse toResponse(GameRes user);

    GameRes toEntity(GameResRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateToEntity(GameResRequest request, @MappingTarget GameRes gameRes);
}
