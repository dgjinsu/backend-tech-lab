package com.budget.api.domain.user.service;

import com.budget.api.domain.user.dto.UserResponse;
import com.budget.api.domain.user.dto.UserSignupRequest;
import com.budget.api.domain.user.entity.User;
import com.budget.api.domain.user.repository.UserRepository;
import com.budget.api.global.exception.CustomException;
import com.budget.api.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User createTestUser() {
        User user = User.create("test@example.com", "encodedPassword", "테스터");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private UserSignupRequest createSignupRequest(String email, String password, String nickname) {
        UserSignupRequest request = new UserSignupRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);
        ReflectionTestUtils.setField(request, "nickname", nickname);
        return request;
    }

    @Test
    @DisplayName("signup_성공_유저생성")
    void signup_성공_유저생성() {
        // given
        UserSignupRequest request = createSignupRequest("test@example.com", "Password1!", "테스터");

        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(passwordEncoder.encode("Password1!")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedUser, "id", 1L);
            return savedUser;
        });

        // when
        UserResponse response = userService.signup(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getNickname()).isEqualTo("테스터");

        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("Password1!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("signup_중복이메일_예외발생")
    void signup_중복이메일_예외발생() {
        // given
        UserSignupRequest request = createSignupRequest("duplicate@example.com", "Password1!", "테스터");

        given(userRepository.existsByEmail("duplicate@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
                });
    }

    @Test
    @DisplayName("getUser_성공_유저반환")
    void getUser_성공_유저반환() {
        // given
        User user = createTestUser();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUser(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getNickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("getUser_존재하지않는유저_예외발생")
    void getUser_존재하지않는유저_예외발생() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(999L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }
}
