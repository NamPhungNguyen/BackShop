package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.request.order.CreateOrderRequest;
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
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        // Call the service to create an order and return the response
        return orderService.createOrder(createOrderRequest);
    }
}
