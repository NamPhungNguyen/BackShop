package com.appshop.back_shop.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long productId;

    @Column(nullable = false)
    String name;

    @Column(length = 500)
    String description;

    @Column(nullable = false)
    BigDecimal price;

    @Column(nullable = false)
    int stock;

    @Column(nullable = false)
    String size;

    @Column(nullable = false)
    String color;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
