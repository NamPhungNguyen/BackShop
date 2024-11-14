package com.appshop.back_shop.mapper;

import com.appshop.back_shop.domain.Comment;
import com.appshop.back_shop.dto.response.comment.CommentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentResponse toResponse(Comment comment);
}
