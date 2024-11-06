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

        if (address.getFullName() != null) {
            existingAddress.setFullName(address.getFullName());
        }
        if (address.getPhoneNumber() != null) {
            existingAddress.setPhoneNumber(address.getPhoneNumber());
        }
        if (address.getAddressDetail() != null) {
            existingAddress.setAddressDetail(address.getAddressDetail());
        }
        if (address.getAdditionalAddress() != null) {
            existingAddress.setAdditionalAddress(address.getAdditionalAddress());
        }
        if (address.getProvince() != null) {
            existingAddress.setProvince(address.getProvince());
        }
        if (address.getCity() != null) {
            existingAddress.setCity(address.getCity());
        }
        if (address.getCountry() != null) {
            existingAddress.setCountry(address.getCountry());
        }
        if (address.getIsDefault() != null) {
            existingAddress.setIsDefault(address.getIsDefault());
        }

        return addressRepository.save(existingAddress);
    }

    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    public ShippingAddress setDefaultAddress(Long addressId) {
        List<ShippingAddress> addresses = addressRepository.findByUserId(getUserIdFromToken());

        boolean addressFound = false;

        for (ShippingAddress address : addresses) {
            if (address.getAddressId().equals(addressId)) {
                address.setIsDefault(true);
                addressFound = true;
            } else {
                address.setIsDefault(false);
            }
            addressRepository.save(address);
        }
        if (!addressFound) {
            throw new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND);
        }

        return addressRepository.findById(addressId).orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));
    }


    public List<ShippingAddress> getUserAddresses() {
        Long userId = getUserIdFromToken();
        return addressRepository.findByUserId(userId);
    }
}
