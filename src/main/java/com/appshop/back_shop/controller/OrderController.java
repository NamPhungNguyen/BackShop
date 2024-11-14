package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.order.OrderCancelResponse;
import com.appshop.back_shop.dto.response.order.OrderResponse;
import com.appshop.back_shop.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Tag(name = "Order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping("/create")
    ApiResponse<OrderResponse> createOrder(@RequestParam String paymentMethod, @RequestParam(required = false) String couponCode, @RequestParam Long addressId) {
        return ApiResponse.<OrderResponse>builder().code(200).result(orderService.createOrder(paymentMethod, couponCode, addressId)).build();
    }

    @PutMapping("/cancel/{orderId}")
    ApiResponse<OrderCancelResponse> cancelOrder(@PathVariable Long orderId) {
        return ApiResponse.<OrderCancelResponse>builder()
                .result(orderService.cancelOrder(orderId))
                .code(200)
                .build();
    }

    @PutMapping("/{orderId}/status")
    ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .result(orderService.updateOrderStatus(orderId, status))
                .build();
    }

    @GetMapping("completed")
    ApiResponse<List<OrderResponse>> getCompletedOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .code(200)
                .result(orderService.getCompletedOrdersByUser())
                .build();
    }

    @GetMapping("not-completed")
    ApiResponse<List<OrderResponse>> getNotCompletedOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .code(200)
                .result(orderService.getNotCompletedOrdersByUser())
                .build();
    }
}
