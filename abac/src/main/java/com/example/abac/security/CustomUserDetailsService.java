package com.example.abac.security;

import com.example.abac.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// [학습 포인트] Spring Security가 로그인 요청을 받았을 때 '이 username 어디서 꺼내오지?'에 답하는 구현체.
// DaoAuthenticationProvider가 AuthenticationManager 안에서 이 Bean을 찾아 호출한다 → 직접 호출할 일은 없음.
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // User 엔티티 → CustomUserPrincipal(UserDetails)로 변환. password(해시)는 그대로 넘겨서
        // AuthenticationManager가 BCrypt 매칭에 사용하게 한다.
        return userRepository.findByUsername(username)
                .map(CustomUserPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
