package com.example.abac.security.jwt;

import com.example.abac.domain.user.Role;
import com.example.abac.security.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_DEPARTMENT_ID = "departmentId";

    private final SecretKey key;
    private final long expireMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expire-ms}") long expireMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMs = expireMs;
    }

    public String generate(CustomUserPrincipal principal) {
        Date now = new Date();
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim(CLAIM_USER_ID, principal.getUserId())
                .claim(CLAIM_ROLE, principal.getRole().name())
                .claim(CLAIM_DEPARTMENT_ID, principal.getDepartmentId())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireMs))
                .signWith(key)
                .compact();
    }

    public CustomUserPrincipal parse(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        Claims c = jws.getPayload();
        Long userId = c.get(CLAIM_USER_ID, Number.class).longValue();
        Role role = Role.valueOf(c.get(CLAIM_ROLE, String.class));
        Long departmentId = c.get(CLAIM_DEPARTMENT_ID, Number.class).longValue();
        return new CustomUserPrincipal(userId, c.getSubject(), "", role, departmentId);
    }
}
