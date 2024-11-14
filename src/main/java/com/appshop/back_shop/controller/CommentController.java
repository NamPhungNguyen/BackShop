package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.Comment;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.comment.CommentResponse;
import com.appshop.back_shop.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@Tag(name = "Comment")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("{productId}")
    ResponseEntity<String> addComment(@PathVariable Long productId, @RequestParam String content, @RequestParam Integer rating, @RequestParam(required = false) List<String> imageUrls) {
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        try {
            commentService.addComment(productId, content, rating, imageUrls);
            return ResponseEntity.ok("Comment added and rating updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding comment: " + e.getMessage());
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> editComment(
            @PathVariable Long commentId,
            @RequestParam String content,
            @RequestParam Integer rating,
            @RequestParam(required = false) List<String> imageUrls) {

        commentService.editComment(commentId, content, rating, imageUrls);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("list/{productId}")
    public ApiResponse<List<CommentResponse>> getListComment(@PathVariable Long productId) {
        List<CommentResponse> comments = commentService.getCommentsByProduct(productId);

        return ApiResponse.<List<CommentResponse>>builder()
                .result(comments)
                .build();
    }
}
