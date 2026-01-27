package com.rawsaurus.sleep_not_included.game_res.mapper;

import com.rawsaurus.sleep_not_included.game_res.dto.GameResRequest;
import com.rawsaurus.sleep_not_included.game_res.dto.GameResResponse;
import com.rawsaurus.sleep_not_included.game_res.model.GameRes;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface GameResMapper {

    GameResResponse toResponse(GameRes user);

    GameRes toEntity(GameResRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateToEntity(GameResRequest request, @MappingTarget GameRes gameRes);
}
