package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Cart;
import com.appshop.back_shop.domain.CartItem;
import com.appshop.back_shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCart_CartIdAndCartItemId(Long cartId, Long itemId);
    Optional<CartItem> findByCartAndProductAndSizeAndColor(Cart cart, Product product, String size, String color);
    List<CartItem> findByCart_User_IdAndCheckedOutTrue(Long userId);
    Optional<CartItem> findByCart_User_IdAndProduct_ProductId(Long userId, Long productId);
}
