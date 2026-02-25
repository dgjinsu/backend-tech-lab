package com.budget.api.domain.user.service;

import com.budget.api.domain.user.dto.LoginRequest;
import com.budget.api.domain.user.dto.RefreshRequest;
import com.budget.api.domain.user.dto.TokenResponse;
import com.budget.api.domain.user.entity.User;
import com.budget.api.domain.user.repository.UserRepository;
import com.budget.api.global.exception.CustomException;
import com.budget.api.global.exception.ErrorCode;
import com.budget.api.global.jwt.JwtTokenProvider;
import com.budget.api.infra.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisService redisService;

    private User createTestUser() {
        User user = User.create("test@example.com", "encodedPassword", "테스터");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private LoginRequest createLoginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }

    private RefreshRequest createRefreshRequest(String refreshToken) {
        RefreshRequest request = new RefreshRequest();
        ReflectionTestUtils.setField(request, "refreshToken", refreshToken);
        return request;
    }

    @Test
    @DisplayName("login_성공_토큰반환")
    void login_성공_토큰반환() {
        // given
        User user = createTestUser();
        LoginRequest request = createLoginRequest("test@example.com", "rawPassword");

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("rawPassword", "encodedPassword")).willReturn(true);
        given(jwtTokenProvider.generateAccessToken(1L, "test@example.com")).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken(1L)).willReturn("refresh-token");
        given(jwtTokenProvider.getRefreshTokenExpiry()).willReturn(604800000L);
        given(jwtTokenProvider.getAccessTokenExpiry()).willReturn(1800000L);

        // when
        TokenResponse response = authService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getExpiresIn()).isEqualTo(1800L);
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");

        verify(redisService).set(
                eq("RT:1"),
                eq("refresh-token"),
                eq(604800000L),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("login_존재하지않는이메일_예외발생")
    void login_존재하지않는이메일_예외발생() {
        // given
        LoginRequest request = createLoginRequest("unknown@example.com", "password");

        given(userRepository.findByEmail("unknown@example.com")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
                });
    }

    @Test
    @DisplayName("login_잘못된비밀번호_예외발생")
    void login_잘못된비밀번호_예외발생() {
        // given
        User user = createTestUser();
        LoginRequest request = createLoginRequest("test@example.com", "wrongPassword");

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
                });
    }

    @Test
    @DisplayName("refresh_성공_새토큰반환")
    void refresh_성공_새토큰반환() {
        // given
        User user = createTestUser();
        RefreshRequest request = createRefreshRequest("valid-refresh-token");

        given(jwtTokenProvider.validateToken("valid-refresh-token")).willReturn(true);
        given(jwtTokenProvider.getUserIdFromToken("valid-refresh-token")).willReturn(1L);
        given(redisService.get("RT:1")).willReturn("valid-refresh-token");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(jwtTokenProvider.generateAccessToken(1L, "test@example.com")).willReturn("new-access-token");
        given(jwtTokenProvider.generateRefreshToken(1L)).willReturn("new-refresh-token");
        given(jwtTokenProvider.getRefreshTokenExpiry()).willReturn(604800000L);
        given(jwtTokenProvider.getAccessTokenExpiry()).willReturn(1800000L);

        // when
        TokenResponse response = authService.refresh(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(response.getExpiresIn()).isEqualTo(1800L);
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");

        verify(redisService).set(
                eq("RT:1"),
                eq("new-refresh-token"),
                eq(604800000L),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    @DisplayName("refresh_유효하지않은토큰_예외발생")
    void refresh_유효하지않은토큰_예외발생() {
        // given
        RefreshRequest request = createRefreshRequest("invalid-token");

        given(jwtTokenProvider.validateToken("invalid-token")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
                });
    }

    @Test
    @DisplayName("refresh_Redis에없는토큰_예외발생")
    void refresh_Redis에없는토큰_예외발생() {
        // given
        RefreshRequest request = createRefreshRequest("orphan-refresh-token");

        given(jwtTokenProvider.validateToken("orphan-refresh-token")).willReturn(true);
        given(jwtTokenProvider.getUserIdFromToken("orphan-refresh-token")).willReturn(1L);
        given(redisService.get("RT:1")).willReturn(null);

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
                });
    }

    @Test
    @DisplayName("refresh_Redis에다른토큰저장_예외발생")
    void refresh_Redis에다른토큰저장_예외발생() {
        // given
        RefreshRequest request = createRefreshRequest("old-refresh-token");

        given(jwtTokenProvider.validateToken("old-refresh-token")).willReturn(true);
        given(jwtTokenProvider.getUserIdFromToken("old-refresh-token")).willReturn(1L);
        given(redisService.get("RT:1")).willReturn("different-stored-token");

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
                });
    }

    @Test
    @DisplayName("refresh_존재하지않는사용자_예외발생")
    void refresh_존재하지않는사용자_예외발생() {
        // given
        RefreshRequest request = createRefreshRequest("valid-refresh-token");

        given(jwtTokenProvider.validateToken("valid-refresh-token")).willReturn(true);
        given(jwtTokenProvider.getUserIdFromToken("valid-refresh-token")).willReturn(999L);
        given(redisService.get("RT:999")).willReturn("valid-refresh-token");
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }
}
