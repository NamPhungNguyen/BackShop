package com.appshop.back_shop.dto.response.comment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    Long commentId;
    String content;
    int rating;
    List<String> imageUrls;
    Long productId;
    Long userId;
    String fullName;
    String profileImgUrl;
    LocalDateTime createdAt;
    LocalDateTime updateAt;
}
