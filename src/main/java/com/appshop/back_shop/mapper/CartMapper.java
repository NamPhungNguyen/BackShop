package com.appshop.back_shop.mapper;

import com.appshop.back_shop.domain.Cart;
import com.appshop.back_shop.domain.CartItem;
import com.appshop.back_shop.dto.response.Cart.CartForUserResponse;
import com.appshop.back_shop.dto.response.Cart.CartItemResponse;
import com.appshop.back_shop.dto.response.Cart.CartResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface CartMapper {
    CartResponse toResponse(Cart cart);
}
