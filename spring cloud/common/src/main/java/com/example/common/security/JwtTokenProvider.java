package com.example.common.security;

import com.example.common.role.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 120L; // 120 days
    private final Key key;

    @Autowired
    public JwtTokenProvider(@Value("${app.auth.secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 복호화
     */
    public Claims get(String jwt) throws JwtException {
        return Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jwt)
            .getBody();
    }

    /**
     * 토큰 만료 여부 체크
     * @return true : 만료됨, false : 만료되지 않음
     */
    public boolean isExpiration(String jwt) throws JwtException {
        log.info("토큰 만료 여부 체크");
        try {
            return get(jwt).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // 토큰에서 loginId 추출
    public String getMemberId(String jwt) {
        return get(jwt).get("memberId", String.class);  // loginId 클레임에서 추출
    }

    // 토큰에서 roles 추출
    public Role getRoles(String jwt) {
        String roleString = get(jwt).get("role", String.class);
        return Role.valueOf(roleString);  // Role Enum일 경우
    }
}
