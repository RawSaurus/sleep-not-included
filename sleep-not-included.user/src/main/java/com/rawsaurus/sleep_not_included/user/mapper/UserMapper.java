package com.rawsaurus.sleep_not_included.user.mapper;

import com.rawsaurus.sleep_not_included.user.dto.UserRequest;
import com.rawsaurus.sleep_not_included.user.dto.UserResponse;
import com.rawsaurus.sleep_not_included.user.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserRequest request);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateToEntity(UserRequest request, @MappingTarget User user);
}
