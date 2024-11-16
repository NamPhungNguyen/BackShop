package com.appshop.back_shop.dto.request.comment;

import lombok.Data;

import java.util.List;

@Data
public class CommentRequest {
    private String content;
    private Integer rating;
    private List<String> imageUrls;
}
