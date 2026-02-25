package com.budget.api.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String SECRET = "test-secret-key-for-jwt-token-generation-testing-2026";
    private static final long ACCESS_TOKEN_EXPIRY = 1800000L; // 30분
    private static final long REFRESH_TOKEN_EXPIRY = 604800000L; // 7일

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiry", ACCESS_TOKEN_EXPIRY);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiry", REFRESH_TOKEN_EXPIRY);
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("generateAccessToken_정상입력_유효한토큰생성")
    void generateAccessToken_정상입력_유효한토큰생성() {
        // given
        Long userId = 1L;
        String email = "test@example.com";

        // when
        String token = jwtTokenProvider.generateAccessToken(userId, email);

        // then
        assertThat(token).isNotNull().isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(userId);
    }

    @Test
    @DisplayName("generateAccessToken_정상입력_이메일클레임포함")
    void generateAccessToken_정상입력_이메일클레임포함() {
        // given
        Long userId = 1L;
        String email = "test@example.com";

        // when
        String token = jwtTokenProvider.generateAccessToken(userId, email);

        // then
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String extractedEmail = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("generateRefreshToken_정상입력_유효한토큰생성")
    void generateRefreshToken_정상입력_유효한토큰생성() {
        // given
        Long userId = 1L;

        // when
        String token = jwtTokenProvider.generateRefreshToken(userId);

        // then
        assertThat(token).isNotNull().isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(userId);
    }

    @Test
    @DisplayName("getUserIdFromToken_유효한토큰_올바른UserId반환")
    void getUserIdFromToken_유효한토큰_올바른UserId반환() {
        // given
        Long userId = 42L;
        String token = jwtTokenProvider.generateAccessToken(userId, "test@example.com");

        // when
        Long extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

        // then
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("validateToken_유효한토큰_true반환")
    void validateToken_유효한토큰_true반환() {
        // given
        String token = jwtTokenProvider.generateAccessToken(1L, "test@example.com");

        // when
        boolean result = jwtTokenProvider.validateToken(token);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("validateToken_만료된토큰_false반환")
    void validateToken_만료된토큰_false반환() {
        // given
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        String expiredToken = Jwts.builder()
                .subject("1")
                .issuedAt(new Date(now.getTime() - 10000))
                .expiration(new Date(now.getTime() - 5000)) // 이미 만료
                .signWith(key)
                .compact();

        // when
        boolean result = jwtTokenProvider.validateToken(expiredToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("validateToken_잘못된형식의토큰_false반환")
    void validateToken_잘못된형식의토큰_false반환() {
        // given
        String malformedToken = "this.is.not.a.valid.jwt";

        // when
        boolean result = jwtTokenProvider.validateToken(malformedToken);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("validateToken_다른키로서명된토큰_false반환")
    void validateToken_다른키로서명된토큰_false반환() {
        // given
        SecretKey differentKey = Keys.hmacShaKeyFor(
                "different-secret-key-for-testing-wrong-signature-2026".getBytes(StandardCharsets.UTF_8));
        String tokenWithWrongKey = Jwts.builder()
                .subject("1")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(differentKey)
                .compact();

        // when
        boolean result = jwtTokenProvider.validateToken(tokenWithWrongKey);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("validateToken_null토큰_false반환")
    void validateToken_null토큰_false반환() {
        // when
        boolean result = jwtTokenProvider.validateToken(null);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("validateToken_빈문자열토큰_false반환")
    void validateToken_빈문자열토큰_false반환() {
        // when
        boolean result = jwtTokenProvider.validateToken("");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("getAccessTokenExpiry_설정값반환")
    void getAccessTokenExpiry_설정값반환() {
        // when
        long expiry = jwtTokenProvider.getAccessTokenExpiry();

        // then
        assertThat(expiry).isEqualTo(ACCESS_TOKEN_EXPIRY);
    }

    @Test
    @DisplayName("getRefreshTokenExpiry_설정값반환")
    void getRefreshTokenExpiry_설정값반환() {
        // when
        long expiry = jwtTokenProvider.getRefreshTokenExpiry();

        // then
        assertThat(expiry).isEqualTo(REFRESH_TOKEN_EXPIRY);
    }
}
