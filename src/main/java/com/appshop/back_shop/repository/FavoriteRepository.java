package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Favorite;
import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndProduct(User user, Product product);
    @Query("SELECT f FROM Favorite f JOIN FETCH f.product WHERE f.user = :user")
    List<Favorite> findAllByUser(User user);
    void deleteByUserAndProduct(User user, Product product);
}
