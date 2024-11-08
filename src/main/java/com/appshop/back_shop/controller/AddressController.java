package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.ShippingAddress;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.service.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/shipping-address")
@Tag(name = "Address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {
    AddressService addressService;

    @PostMapping("/add")
    public ApiResponse<ShippingAddress> addAddress(@RequestBody ShippingAddress shippingAddress) {
        return ApiResponse.<ShippingAddress>builder()
                .code(200)
                .message("Success")
                .result(addressService.addAddress(shippingAddress))
                .build();
    }

    @PutMapping("/update/{addressId}")
    public ApiResponse<ShippingAddress> updateAddress(@PathVariable Long addressId, @RequestBody ShippingAddress updateAddress) {
        return ApiResponse.<ShippingAddress>builder()
                .code(200)
                .message("Success")
                .result(addressService.updateAddress(addressId, updateAddress))
                .build();
    }

    @DeleteMapping("/delete/{addressId}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }

    @PutMapping("/set-default/{addressId}")
    public ApiResponse<ShippingAddress> setDefaultAddress(@PathVariable Long addressId) {
        return ApiResponse.<ShippingAddress>builder()
                .code(200)
                .result(addressService.setDefaultAddress(addressId))
                .build();
    }

    @GetMapping("/addresses")
    public ApiResponse<List<ShippingAddress>> getUserAddresses() {
        List<ShippingAddress> addresses = addressService.getUserAddresses();
        return ApiResponse.<List<ShippingAddress>>builder()
                .code(200)
                .result(addresses)
                .build();
    }

    @GetMapping("/default")
    public ApiResponse<ShippingAddress> getUserDefaultAddresses() {
        return ApiResponse.<ShippingAddress>builder()
                .code(200)
                .result(addressService.getUserAddressDefault())
                .build();
    }

}
