package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Cart;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.response.Cart.CartResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.mapper.CartMapper;
import com.appshop.back_shop.repository.CartRepository;
import com.appshop.back_shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    UserRepository userRepository;
    CartMapper cartMapper;

    public CartResponse createCart(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = new Cart();

        cart.setUser(user);
        cart.setCreatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toResponse(cart);
    }
}
