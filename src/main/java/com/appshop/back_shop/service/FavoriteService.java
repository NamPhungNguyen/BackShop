package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Favorite;
import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.response.Product.ProductResponse;
import com.appshop.back_shop.repository.FavoriteRepository;
import com.appshop.back_shop.repository.ProductRepository;
import com.appshop.back_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    public void addFavorite(Long productId) {
        User user = userRepository.findById(getUserIdFromToken()).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Favorite favorite = new Favorite(null, user, product);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long productId) {
        User user = userRepository.findById(getUserIdFromToken()).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        favoriteRepository.deleteByUserAndProduct(user, product);
    }
    @Transactional(readOnly = true)
    public List<ProductResponse> getFavoriteProducts() {
        User user = userRepository.findById(getUserIdFromToken()).orElseThrow(() -> new RuntimeException("User not found"));

        return favoriteRepository.findAllByUser(user).stream()
                .map(favorite -> {
                    Product product = favorite.getProduct();
                    return ProductResponse.builder()
                            .productId(product.getProductId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .discount(product.getDiscount())
                            .stock(product.getStock())
                            .size(product.getSize())
                            .color(product.getColor())
                            .isAvailable(product.isAvailable())
                            .rating(product.getRating())
                            .ratingCount(product.getRatingCount())
                            .brand(product.getBrand())
                            .productCode(product.getProductCode())
                            .imgProduct(product.getImgProduct())
                            .categoryId(product.getCategory().getCategoryId())
                            .categoryName(product.getCategory().getName())
                            .createdAt(product.getCreatedAt())
                            .updatedAt(product.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }


}
