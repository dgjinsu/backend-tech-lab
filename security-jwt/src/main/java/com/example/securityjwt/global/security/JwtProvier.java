package com.example.securityjwt.global.security;

import com.example.securityjwt.domain.member.entity.Role;
import com.example.securityjwt.global.security.dto.MemberAuthInfo;
import com.example.securityjwt.global.security.property.TokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.time.ZonedDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * [JWT 관련 메서드를 제공하는 클래스]
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvier {

    private final TokenProperties tokenProperties;

    /**
     * Access Token 생성
     */
    public String createAccessToken(Long memberId, Role role) {
        return createToken(memberId, role, tokenProperties.getAccessTokenExpTime());
    }

    private String createToken(Long memberId, Role role, long expireTime) {
        Claims claims = createClaims(memberId, role);

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date.from(now.toInstant()))
            .setExpiration(Date.from(tokenValidity.toInstant()))
            .signWith(tokenProperties.getKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    private Claims createClaims(Long memberId, Role role) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.put("role", role);
        return claims;
    }

    /**
     * Token에서 User ID 추출
     */
    public Long getMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    /**
     * Token에서 Role 추출
     */
    public String getMemberRole(String token) {
        return parseClaims(token).get("role", String.class);
    }


    /**
     * JWT 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(tokenProperties.getKey()).build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }


    /**
     * JWT Claims 추출
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(tokenProperties.getKey()).build()
                .parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
