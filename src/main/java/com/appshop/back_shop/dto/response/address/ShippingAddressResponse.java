package com.appshop.back_shop.dto.response.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ShippingAddressResponse {
    private Long addressId;
    private String fullName;
    private String phoneNumber;
    private String addressDetail;
    private String additionalAddress;
    private String province;
    private String city;
    private String country;
    private Boolean isDefault;
}
