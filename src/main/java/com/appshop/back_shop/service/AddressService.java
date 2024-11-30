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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        List<ShippingAddress> userAddresses = addressRepository.findByUserId(user.getId());
        if (userAddresses.isEmpty()) {
            // Địa chỉ đầu tiên, tự động đặt làm mặc định
            address.setIsDefault(true);
        } else if (address.getIsDefault() != null && address.getIsDefault()) {
            // Nếu người dùng chọn địa chỉ này là mặc định, xóa các địa chỉ mặc định khác
            clearOtherDefaultAddresses(user.getId());
        }

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

        if (address.getIsDefault() != null && address.getIsDefault()) {
            clearOtherDefaultAddresses(existingAddress.getUser().getId());
            existingAddress.setIsDefault(true);
        }

        return addressRepository.save(existingAddress);
    }

    public void deleteAddress(Long addressId) {
        ShippingAddress addressToDelete = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));

        if (addressToDelete.getIsDefault() != null && addressToDelete.getIsDefault()) {
            throw new AppException(ErrorCode.CANNOT_DELETE_DEFAULT_ADDRESS);
        }

        addressRepository.deleteById(addressId);
    }

    public ShippingAddress setDefaultAddress(Long addressId) {
        List<ShippingAddress> addresses = addressRepository.findByUserId(getUserIdFromToken());

        ShippingAddress addressToSetDefault = addresses.stream()
                .filter(address -> address.getAddressId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));

        for (ShippingAddress address : addresses) {
            if (address.getIsDefault() != null && address.getIsDefault()) {
                address.setIsDefault(false);
                addressRepository.save(address);
            }
        }

        addressToSetDefault.setIsDefault(true);
        return addressRepository.save(addressToSetDefault);
    }

    public List<ShippingAddress> getUserAddresses() {
        Long userId = getUserIdFromToken();

        List<ShippingAddress> addresses = addressRepository.findByUserId(userId);

        if (addresses.isEmpty()) {
            return Collections.emptyList();
        }

        return addresses;
    }

    public List<ShippingAddress> getUserAddressDefault() {
        Long userId = getUserIdFromToken();

        return addressRepository.findByUserId(userId).stream()
                .filter(address -> Boolean.TRUE.equals(address.getIsDefault()))
                .collect(Collectors.toList());
    }

    private void clearOtherDefaultAddresses(Long userId) {
        List<ShippingAddress> userAddresses = addressRepository.findByUserId(userId);
        for (ShippingAddress address : userAddresses) {
            if (address.getIsDefault() != null && address.getIsDefault()) {
                address.setIsDefault(false);
                addressRepository.save(address);
            }
        }
    }
}
