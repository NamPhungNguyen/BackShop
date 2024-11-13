package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Comment;
import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.repository.CommentRepository;
import com.appshop.back_shop.repository.ProductRepository;
import com.appshop.back_shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    CommentRepository commentRepository;

    ProductRepository productRepository;

     UserRepository userRepository;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    // Method to add a comment and update product rating
    public void addComment(Long productId, String content, Integer rating, List<String> imageUrls) {
        // 1. Fetch the product and user
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(getUserIdFromToken())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Create and save the comment
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setRating(rating);  // Assign the rating
        comment.setImageUrls(imageUrls);
        comment.setProduct(product);
        comment.setUser(user);

        commentRepository.save(comment);  // Save the comment

        // 3. Recalculate product rating
        recalculateProductRating(product);
    }

    // Method to recalculate product rating based on its comments
    private void recalculateProductRating(Product product) {
        // 4. Fetch all comments for the product
        List<Comment> comments = commentRepository.findByProduct(product);

        // 5. Calculate the new rating (average of all ratings)
        double totalRating = 0;
        int ratingCount = 0;

        for (Comment comment : comments) {
            Integer commentRating = comment.getRating();
            if (commentRating != null) {
                totalRating += commentRating;
                ratingCount++;
            }
        }

        // 6. Update the product's rating
        if (ratingCount > 0) {
            double averageRating = totalRating / ratingCount;
            product.setRating(averageRating);
        } else {
            product.setRating(0.0);  // No comments, no rating
        }

        // 7. Save the updated product with the new rating
        productRepository.save(product);
    }
}

