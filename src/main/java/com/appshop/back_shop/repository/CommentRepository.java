package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Comment;
import com.appshop.back_shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProduct(Product product);
}
