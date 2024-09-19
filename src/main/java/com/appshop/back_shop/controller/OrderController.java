package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.request.order.UpdateOrderStatusRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.order.OrderDetailResponse;
import com.appshop.back_shop.dto.response.order.OrderResponse;
import com.appshop.back_shop.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
    public ApiResponse<OrderResponse> createOrder() {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder())
                .message("Order created successfully")
                .code(200)
                .build();
    }

    @GetMapping("/user")
    public ApiResponse<List<OrderResponse>> fetchOrdersUserId() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.fetchOrdersByUserId())
                .code(200)
                .message("Orders retrieved successfully")
                .build();
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> fetchOrderById(@PathVariable Long orderId){
        return ApiResponse.<OrderDetailResponse>builder()
                .message("get order detail successful")
                .code(200)
                .result(orderService.fetchOrderDetailsById(orderId))
                .build();
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Long orderId, @RequestBody UpdateOrderStatusRequest request){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderId, request.getStatus()))
                .code(200)
                .message("Updated status order successfully")
                .build();
    }

    @DeleteMapping("/{orderId}/delete")
    public ApiResponse<Void> deleteOrder(@PathVariable Long orderId){
        orderService.deleteOrder(orderId);
        return ApiResponse.<Void>builder()
                .message("Sucessfullu deleted order")
                .code(200)
                .build();
    }

}
