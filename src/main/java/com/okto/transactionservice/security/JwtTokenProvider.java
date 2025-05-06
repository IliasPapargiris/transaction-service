package com.okto.transactionservice.security;

import com.okto.transactionservice.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.audience}")
    private String audience;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(CustomUserDetails userDetails) {
        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        log.info("🔐 Generating token for userId={}, email={}, authorities={}",
                userDetails.getUserId(), userDetails.getUsername(), userDetails.getAuthorities());

        // Extract role from authorities (e.g. ROLE_ADMIN)
        String role = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");

        return Jwts.builder()
                .setId(jti)
                .setSubject(String.valueOf(userDetails.getUserId()))
                .claim("email", userDetails.getUsername())
                .claim("role", role)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            boolean expired = claims.getExpiration().before(new Date());

            if (expired) {
                log.warn("❌ Token expired for subject={}, exp={}", claims.getSubject(), claims.getExpiration());
                return false;
            }

            return claims.getIssuer().equals(issuer)
                   && claims.getAudience().equals(audience);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("❌ Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return getClaims(token).get("email", String.class);
    }

    public Long extractUserId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
