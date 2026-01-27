package com.rawsaurus.sleep_not_included.comment.mapper;

import com.rawsaurus.sleep_not_included.comment.dto.CommentRequest;
import com.rawsaurus.sleep_not_included.comment.dto.CommentResponse;
import com.rawsaurus.sleep_not_included.comment.model.Comment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toEntity(CommentRequest request);

    CommentResponse toResponse(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateToEntity(CommentRequest request, @MappingTarget Comment comment);
}
