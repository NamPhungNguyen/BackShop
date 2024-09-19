package com.appshop.back_shop.dto.response.payment;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PaymentResponse {
    Long paymentId;
    Long orderId;
    BigDecimal amount;
    String paymentMethod; // 'credit_card', 'paypal', 'bank_transfer'
    String paymentStatus; // 'pending', 'completed', 'failed'
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
