package com.appshop.back_shop.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String poolCode;              // Mã đại diện cho nhóm mã giảm giá
    String code;                  // Mã giảm giá duy nhất
    Double discountAmount;        // Giá trị giảm giá
    LocalDateTime expiryDate;     // Ngày hết hạn
    boolean active;               // Trạng thái hoạt động
    boolean claimed = false;      // Trạng thái đã được claim

    int totalQuantity;            // Tổng số lượng mã trong nhóm
    int remainingQuantity;        // Số lượng mã còn lại

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<UserCoupon> claimedByUsers = new ArrayList<>();  // Danh sách user đã claim mã
}
