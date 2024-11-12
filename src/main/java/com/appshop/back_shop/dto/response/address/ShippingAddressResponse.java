package com.appshop.back_shop.dto.response.address;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShippingAddressResponse {
    private Long id;
    private String street;
    private String city;
    private String country;
}
