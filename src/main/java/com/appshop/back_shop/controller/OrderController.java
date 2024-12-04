package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.request.order.OrderRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.order.OrderCancelResponse;
import com.appshop.back_shop.dto.response.order.OrderListAllResponse;
import com.appshop.back_shop.dto.response.order.OrderPageResponse;
import com.appshop.back_shop.dto.response.order.OrderResponse;
import com.appshop.back_shop.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
@Tag(name = "Order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping("/create")
    ApiResponse<OrderResponse> createOrder(
            @RequestBody OrderRequest orderRequest // Accepting request body
    ) {
        OrderResponse orderResponse = orderService.createOrder(
                orderRequest.getCouponCode(),
                orderRequest.getAddressId(),
                orderRequest.getPaymentMethod()
        );

        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .result(orderResponse)
                .build();
    }

    @GetMapping("/orders/search")
    public Page<OrderPageResponse> searchOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String addressDetail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return orderService.searchOrders(status, startDate, endDate, fullName, phoneNumber, addressDetail, page, size);
    }


    @GetMapping("/orders")
    public ResponseEntity<Page<OrderPageResponse>> getOrdersByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {
        Page<OrderPageResponse> orders = orderService.getOrdersByStatus(status, page, size);
        return ResponseEntity.ok(orders);
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

    @GetMapping("/list-all")
    public List<OrderListAllResponse> getAllOrders() {
        return orderService.getAllOrdersByUser();
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
