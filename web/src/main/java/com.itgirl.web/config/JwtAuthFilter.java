package com.itgirl.web.config;

import com.itgirl.web.dto.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, UUID> redisTemplate;
    private final List<String> publicUrls;
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expirationMillis;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("Filtering request to URI: {}", request.getRequestURI());
        if (publicUrls.contains(request.getRequestURI())) {
            log.debug("Public URL accessed: {}, skipping JWT authentication", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            log.debug("Authorization header: {}", authHeader != null ? "[present]" : "[missing]");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.replace("Bearer ", "").trim();
                if (token.isEmpty()) {
                    log.warn("Empty Bearer token in Authorization header");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
                    return;
                }

                if (Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
                    log.warn("Token found in Redis blacklist");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revoked");
                    return;
                }

                SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                if (claims.getExpiration().before(new Date())) {
                    log.warn("Token expired at {}", claims.getExpiration());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                    return;
                }

                String email = claims.getSubject();
                UUID id = UUID.fromString(claims.get("id", String.class));
                UserPrincipal userPrincipal = new UserPrincipal(id, email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("User with email: {} authenticated successfully", email);
            } else {
                log.warn("Missing or invalid Authorization header");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing authorization header");
                return;
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
    }
}