package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Order;
import com.appshop.back_shop.domain.Payment;
import com.appshop.back_shop.dto.response.payment.PaymentResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.repository.OrderRepository;
import com.appshop.back_shop.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    PaymentRepository paymentRepository;
    OrderRepository orderRepository;

    private boolean isValidPaymentMethod(String paymentMethod){
        return Arrays.asList("credit_card", "paypal", "bank_transfer").contains(paymentMethod);
    }

    public PaymentResponse createPayment(Long orderId, String paymentMethod){
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!isValidPaymentMethod(paymentMethod)){
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(paymentMethod)
                .paymentStatus("pending")
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(savedPayment.getPaymentId())
                .orderId(savedPayment.getOrder().getOrderId())
                .amount(savedPayment.getAmount())
                .paymentMethod(savedPayment.getPaymentMethod())
                .paymentStatus(savedPayment.getPaymentStatus())
                .createdAt(savedPayment.getCreatedAt())
                .updatedAt(savedPayment.getUpdatedAt())
                .build();
    }

    public PaymentResponse fetchPayment(Long paymentId){
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .orderId(payment.getOrder().getOrderId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    public PaymentResponse updatePayment(Long paymentId, String paymentMethod){
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.setPaymentMethod(paymentMethod);

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(savedPayment.getPaymentId())
                .paymentMethod(savedPayment.getPaymentMethod())
                .paymentStatus(savedPayment.getPaymentStatus())
                .amount(savedPayment.getAmount())
                .orderId(savedPayment.getOrder().getOrderId())
                .createdAt(savedPayment.getCreatedAt())
                .updatedAt(savedPayment.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deletePayment(Long paymentId){
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        paymentRepository.delete(payment);
    }
}
