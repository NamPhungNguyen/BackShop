package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Cart;
import com.appshop.back_shop.domain.CartItem;
import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.request.cart.CartItemRequest;
import com.appshop.back_shop.dto.request.checkout.CheckoutRequest;
import com.appshop.back_shop.dto.response.Cart.CartItemResponse;
import com.appshop.back_shop.dto.response.Cart.CartResponse;
import com.appshop.back_shop.dto.response.checkout.CheckoutResponse;
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

import java.math.BigDecimal;
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

    public CartResponse createCart(Long userId) {
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
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProductAndSizeAndColor(cart, product, request.getSize(), request.getColor());
        CartItem cartItem;

        if (existingCartItem.isPresent()) {
            // update existing cart item quantity
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setSize(request.getSize());
            cartItem.setColor(request.getColor());
            cartItem.setCheckedOut(true);
        }
        cartItemRepository.save(cartItem);
        return CartItemResponse.builder().cartItemId(cartItem.getCartItemId()).productId(cartItem.getProduct().getProductId()).quantity(cartItem.getQuantity()).size(cartItem.getSize()).color(cartItem.getColor()).checkedOut(cartItem.isCheckedOut()).build();
    }

    public CartItemResponse updateItemQuantity(Long cartItemId, int newQuantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));
        Product product = cartItem.getProduct();

        BigDecimal discountPrice = product.getPrice().multiply(BigDecimal.valueOf(1 - product.getDiscount().doubleValue() / 100));
        BigDecimal totalPrice = discountPrice.multiply(BigDecimal.valueOf(newQuantity));
        cartItem.setQuantity(newQuantity);

        cartItemRepository.save(cartItem);

        return CartItemResponse.builder().cartItemId(cartItem.getCartItemId()).productId(cartItem.getProduct().getProductId()).size(cartItem.getSize()).color(cartItem.getColor()).quantity(cartItem.getQuantity()).totalPrice(totalPrice).discountPrice(discountPrice).productName(cartItem.getProduct().getName()).imageUrl(cartItem.getProduct().getImgProduct().toString()).discount(cartItem.getProduct().getDiscount()).price(cartItem.getProduct().getPrice()).build();
    }

    public List<CartItemResponse> fetchCartForUser() {
        Long userId = getUserIdFromToken();
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        List<CartItemResponse> cartItemResponse = cartItems.stream().map(cartItem -> {
            Product product = cartItem.getProduct();

            BigDecimal discountPrice = product.getPrice().multiply(BigDecimal.valueOf(1 - product.getDiscount().doubleValue() / 100));
            BigDecimal totalPrice = discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            cartItemRepository.save(cartItem);

            return CartItemResponse.builder().cartItemId(cartItem.getCartItemId()).productId(cartItem.getProduct().getProductId()).size(cartItem.getSize()).color(cartItem.getColor()).quantity(cartItem.getQuantity()).totalPrice(totalPrice).discountPrice(discountPrice).productName(cartItem.getProduct().getName()).imageUrl(!product.getImgProduct().isEmpty() ? product.getImgProduct().get(0) : null).discount(cartItem.getProduct().getDiscount()).price(cartItem.getProduct().getPrice()).build();
        }).collect(Collectors.toList());

        return cartItemResponse;
    }

    public void updateItemsForCheckout(CheckoutRequest request, boolean isSelect) {
        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());

        for (CartItem cartItem : cartItems) {
            cartItem.setCheckedOut(isSelect);
            cartItemRepository.save(cartItem);
        }
    }

    public CheckoutResponse getProductsForCheckout() {
        List<CartItem> selectedItems = cartItemRepository.findByCart_User_IdAndCheckedOutTrue(getUserIdFromToken());

        List<CartItemResponse> cartItemResponses = selectedItems.stream().map(cartItem -> {
            BigDecimal discountPrice = cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
            BigDecimal totalPrice = discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            return CartItemResponse.builder().cartItemId(cartItem.getCartItemId()).productId(cartItem.getProduct().getProductId()).productName(cartItem.getProduct().getName()).imageUrl(!cartItem.getProduct().getImgProduct().isEmpty() ? cartItem.getProduct().getImgProduct().get(0) : null).size(cartItem.getSize()).color(cartItem.getColor()).quantity(cartItem.getQuantity()).discount(cartItem.getProduct().getDiscount()).price(cartItem.getProduct().getPrice()).discountPrice(discountPrice).totalPrice(totalPrice).build();
        }).collect(Collectors.toList());

        BigDecimal totalCheckoutPrice = cartItemResponses.stream().map(CartItemResponse::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CheckoutResponse(cartItemResponses, totalCheckoutPrice);
    }


    public void deleteItemFromCart(Long itemId) {
        Long userId = getUserIdFromToken();
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));
        CartItem cartItem = cartItemRepository.findByCart_CartIdAndCartItemId(cart.getCartId(), itemId).orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
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
