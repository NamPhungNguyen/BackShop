package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Comment;
import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.response.comment.CommentResponse;
import com.appshop.back_shop.mapper.CommentMapper;
import com.appshop.back_shop.repository.CommentRepository;
import com.appshop.back_shop.repository.ProductRepository;
import com.appshop.back_shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    CommentRepository commentRepository;

    ProductRepository productRepository;

    UserRepository userRepository;

    CommentMapper commentMapper;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    public List<CommentResponse> getCommentsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Fetch all comments related to the product
        List<Comment> comments = commentRepository.findByProduct(product);

        // Map each Comment entity to CommentResponse using the CommentMapper
        return comments.stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }


    public void addComment(Long productId, String content, Integer rating, List<String> imageUrls) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(getUserIdFromToken())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setRating(rating);
        comment.setImageUrls(imageUrls);
        comment.setProduct(product);
        comment.setUser(user);

        commentRepository.save(comment);

        recalculateProductRating(product);
    }

    public void editComment(Long commentId, String content, Integer rating, List<String> imageUrls) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(getUserIdFromToken())) {
            throw new RuntimeException("You are not authorized to edit this comment");
        }

        comment.setContent(content);
        comment.setRating(rating);
        comment.setImageUrls(imageUrls);

        commentRepository.save(comment);

        recalculateProductRating(comment.getProduct());
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(getUserIdFromToken())) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);

        recalculateProductRating(comment.getProduct());
    }

    private void recalculateProductRating(Product product) {
        List<Comment> comments = commentRepository.findByProduct(product);

        double totalRating = 0;
        int ratingCount = 0;

        for (Comment comment : comments) {
            Integer commentRating = comment.getRating();
            if (commentRating != null) {
                totalRating += commentRating;
                ratingCount++;
            }
        }

        if (ratingCount > 0) {
            double averageRating = totalRating / ratingCount;
            product.setRating(averageRating);
        } else {
            product.setRating(0.0);
        }

        productRepository.save(product);
    }
}

