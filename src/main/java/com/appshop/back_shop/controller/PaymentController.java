package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.request.payment.PaymentCreateRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.payment.PaymentResponse;
import com.appshop.back_shop.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Payment")
public class PaymentController {
    PaymentService paymentService;


    @PostMapping("create/{orderId}")
    public ApiResponse<PaymentResponse> createPayment(@PathVariable Long orderId, @RequestBody PaymentCreateRequest request){
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.createPayment(orderId, request.getPaymentMethod()))
                .code(200)
                .message("Create payment successful")
                .build();
    }

    @GetMapping("get-payment/{paymentId}")
    public ApiResponse<PaymentResponse> fetchPayment(@PathVariable Long paymentId){
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.fetchPayment(paymentId))
                .code(200)
                .message("Get payment successful")
                .build();
    }

    @PatchMapping("update-payment/{paymentId}")
    public ApiResponse<PaymentResponse> updatePayment(@PathVariable Long paymentId, @RequestBody PaymentCreateRequest request){
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.updatePayment(paymentId, request.getPaymentMethod()))
                .code(200)
                .message("Succcessfully updated payment")
                .build();
    }

    @DeleteMapping("delete-payment/{paymentId}")
    public ApiResponse<Void> deletePayment(@PathVariable Long paymentId){
        paymentService.deletePayment(paymentId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Successfully deleted payment")
                .build();
    }
}
