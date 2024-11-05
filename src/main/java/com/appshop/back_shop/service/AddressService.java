package com.appshop.back_shop.service;


import com.appshop.back_shop.domain.ShippingAddress;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.repository.ShippingAddressRepository;
import com.appshop.back_shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {
    ShippingAddressRepository addressRepository;
    UserRepository userRepository;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    public ShippingAddress addAddress(ShippingAddress address) {
        User user = userRepository.findById(getUserIdFromToken()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        address.setUser(user);
        return addressRepository.save(address);
    }

    public ShippingAddress updateAddress(Long addressId, ShippingAddress address) {
        ShippingAddress existingAddress = addressRepository.findById(addressId).orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_EXITS));

        address.setUser(existingAddress.getUser());

        existingAddress.setFullName(address.getFullName());
        existingAddress.setPhoneNumber(address.getPhoneNumber());
        existingAddress.setAddressDetail(address.getAddressDetail());
        existingAddress.setAdditionalAddress(address.getAdditionalAddress());
        existingAddress.setProvince(address.getProvince());
        existingAddress.setCity(address.getCity());
        existingAddress.setCountry(address.getCountry());

        return addressRepository.save(existingAddress);
    }

    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    public ShippingAddress setDefaultAddress(Long addressId) {
        List<ShippingAddress> addresses = addressRepository.findByUserId(getUserIdFromToken());

        for (ShippingAddress address : addresses) {
            boolean shouldBeDefault = address.getAddressId().equals(addressId);
            if (address.getIsDefault() != shouldBeDefault) {
                address.setIsDefault(shouldBeDefault);
                addressRepository.save(address);
            }
        }

        return addressRepository.findById(addressId).orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));
    }

    public List<ShippingAddress> getUserAddresses() {
        Long userId = getUserIdFromToken();
        return addressRepository.findByUserId(userId);
    }


}
