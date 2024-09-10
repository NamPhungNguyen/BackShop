package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.InValidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InValidatedToken, String> {

}
