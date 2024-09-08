package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.RefreshToken;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.request.AuthenticationRequest;
import com.appshop.back_shop.dto.request.IntrospectRequest;
import com.appshop.back_shop.dto.request.RefreshTokenRequest;
import com.appshop.back_shop.dto.response.AuthenticationResponse;
import com.appshop.back_shop.dto.response.IntrospectResponse;
import com.appshop.back_shop.dto.response.RefreshTokenResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.repository.RefreshTokenRepository;
import com.appshop.back_shop.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }


        var accessToken = generateToken(user);
        var refreshToken = generateRefreshToken(user.getUserId());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(authenticated)
                .build();
    }

    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        RefreshToken storedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (storedRefreshToken.isUsed() || storedRefreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        storedRefreshToken.setAccessTokenValid(false);
        storedRefreshToken.setUsed(true);
        refreshTokenRepository.save(storedRefreshToken);

        User user = userRepository.findById(storedRefreshToken.getUser().getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));


        String newAccessToken = generateToken(user);
        String newRefreshToken = generateRefreshToken(user.getUserId());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        Date expiryTime = claimsSet.getExpirationTime();
        boolean verified = signedJWT.verify(verifier);

        boolean isValid = verified &&
                expiryTime.after(new Date()) &&
                "namphung.com".equals(claimsSet.getIssuer()) &&
                claimsSet.getSubject() != null;

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    private String generateToken(User user) {
        Instant now = Instant.now();
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("namphung.com")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRefreshToken(Long userId) {
        String refreshToken = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plus(30, ChronoUnit.DAYS);

        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, null, expiration);

        refreshTokenEntity.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        refreshTokenRepository.save(refreshTokenEntity);

        return refreshToken;
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(stringJoiner::add);
        }
        return stringJoiner.toString();
    }
}