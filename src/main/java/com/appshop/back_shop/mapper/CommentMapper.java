package com.appshop.back_shop.mapper;

import com.appshop.back_shop.domain.Comment;
import com.appshop.back_shop.dto.response.comment.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "profileImgUrl", source = "user.profileImgUrl")
    CommentResponse toResponse(Comment comment);
}
