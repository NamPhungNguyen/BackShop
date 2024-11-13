package com.appshop.back_shop.controller;

import com.appshop.back_shop.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {
    CommentService commentService;

    @PostMapping("/comments/{productId}")
    ResponseEntity<String> addComment(@PathVariable Long productId, @RequestParam String content, @RequestParam Integer rating, @RequestParam(required = false)List<String> imageUrls) {
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        try {
            // Add the comment and update the product rating
            commentService.addComment(productId, content, rating, imageUrls);

            // Return success response
            return ResponseEntity.ok("Comment added and rating updated successfully");
        } catch (Exception e) {
            // Handle errors (e.g., product or user not found)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding comment: " + e.getMessage());
        }
    }
}
