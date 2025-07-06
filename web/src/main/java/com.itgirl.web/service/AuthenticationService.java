package com.itgirl.web.service;

import com.itgirl.web.client.UserCoreClient;
import com.itgirl.web.dto.AuthenticationRequest;
import com.itgirl.web.dto.AuthenticationResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserCoreClient userCoreClient;
    private final RedisTemplate<String, UUID> redisTemplate;

    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expirationMillis;

    public String authentication(AuthenticationRequest authenticationRequest) {
        log.info("Start authentication user with email: {}", authenticationRequest.getEmail());
        AuthenticationResponse user = userCoreClient.authUser(authenticationRequest);

        if (user == null) {
            log.warn("Authentication failed");
            throw new RuntimeException("Authentication failed");
        }
        log.debug("User authenticated successfully. Generating JWT");

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public void addToBlackList(String token) {
        log.info("Blacklist token: {}", token);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        Claims claims = jws.getPayload();
        UUID id = UUID.fromString(claims.get("id", String.class));
        Date expiration = claims.getExpiration();
        long ttl = expiration.getTime() - System.currentTimeMillis();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(token, id, ttl, TimeUnit.MILLISECONDS);
            log.info("Token blacklist with TTL (ms): {}", ttl);
        }
    }
}