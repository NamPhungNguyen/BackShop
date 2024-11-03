package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Cart;
import com.appshop.back_shop.domain.CartItem;
import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.request.cart.CartItemRequest;
import com.appshop.back_shop.dto.response.Cart.CartForUserResponse;
import com.appshop.back_shop.dto.response.Cart.CartItemResponse;
import com.appshop.back_shop.dto.response.Cart.CartItemUpdateRequest;
import com.appshop.back_shop.dto.response.Cart.CartResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.mapper.CartMapper;
import com.appshop.back_shop.repository.CartItemRepository;
import com.appshop.back_shop.repository.CartRepository;
import com.appshop.back_shop.repository.ProductRepository;
import com.appshop.back_shop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    UserRepository userRepository;
    CartMapper cartMapper;
    ProductRepository productRepository;
    CartItemRepository cartItemRepository;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public CartResponse getCartForUser() {
        User user = userRepository.findById(getUserIdFromToken()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user).orElse(null);

        if (cart == null) {
            return null;
        }

        CartResponse cartResponse = cartMapper.toResponse(cart);
        cartResponse.setUserId(user.getId());
        return cartResponse;
    }

    public CartResponse createCart() {
        Long userId = getUserIdFromToken();

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (cartRepository.existsByUser(user)) throw new AppException(ErrorCode.CART_ALREADY_EXISTS);

        Cart cart = new Cart();

        cart.setUser(user);
        cart.setCreatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);

        CartResponse cartResponse = cartMapper.toResponse(savedCart);
        cartResponse.setUserId(cart.getUser().getId());

        return cartResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional// ti note cai nay lai
    public void deleteCartByUserId(Long userId) {
        cartRepository.deleteByUser_Id(userId);
    }

    public CartItemResponse addItemToCart(CartItemRequest request) {
        Long userId = getUserIdFromToken();

        // Retrieve the user's cart
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));

        // Retrieve the product
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Check if an item with the same product, size, and color already exists
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProductAndSizeAndColor(cart, product, request.getSize(), request.getColor());

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            // If it exists and has the same size and color, update the quantity
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            // Create a new CartItem for the new combination of size/color
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setSize(request.getSize());
            cartItem.setColor(request.getColor());
        }

        // Save the cart item (either updated or new)
        cartItemRepository.save(cartItem);

        // Build and return the response
        return CartItemResponse.builder()
                .cartItemId(cartItem.getCartItemId())
                .productId(cartItem.getProduct().getProductId())
                .quantity(cartItem.getQuantity())
                .size(cartItem.getSize())
                .color(cartItem.getColor())
                .build();
    }



    public CartForUserResponse fetchCartForUser() {
        Long userId = getUserIdFromToken();

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        CartForUserResponse cartForUserResponse = new CartForUserResponse();

        cartForUserResponse.setCartId(cart.getCartId());
        cartForUserResponse.setUserId(cart.getUser().getId());
        cartForUserResponse.setCreateAt(cart.getCreatedAt());

        List<CartItemResponse> cartItemResponses = cartItems.stream().map(cartItem -> {
            CartItemResponse itemResponse = new CartItemResponse();
            itemResponse.setCartItemId(cartItem.getCartItemId());
            itemResponse.setProductId(cartItem.getProduct().getProductId());
            itemResponse.setQuantity(cartItem.getQuantity());
            return itemResponse;
        }).collect(Collectors.toList());

        cartForUserResponse.setItems(cartItemResponses);

        return cartForUserResponse;

    }

    public CartItemResponse updateCartItem(Long productId, CartItemUpdateRequest request) {
        Long userId = getUserIdFromToken();

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));

        CartItem cartItem = cartItemRepository.findByCartAndProduct_ProductId(cart, productId).orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        return CartItemResponse.builder().cartItemId(cartItem.getCartItemId()).productId(cartItem.getProduct().getProductId()).quantity(cartItem.getQuantity()).build();
    }

    public void deleteItemFromCart(Long cartId, Long itemId) {
        Long userId = getUserIdFromToken();

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));

        if (!cart.getUser().getId().equals(userId)) throw new AppException(ErrorCode.USER_NOT_AUTHORIZED);

        CartItem cartItem = cartItemRepository.findByCart_CartIdAndCartItemId(cartId, itemId).orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItemRepository.delete(cartItem);
    }

    public void clearAllItemsFromCart(Long cartId) {
        Long userId = getUserIdFromToken();

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));

        if (!cart.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.USER_NOT_AUTHORIZED);
        }

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        cartItemRepository.deleteAll(cartItems);
    }
}
