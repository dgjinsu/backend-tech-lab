package com.example.abac.config;

import com.example.abac.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// [학습 지도] Spring Security 설정의 중심. 여기서 FilterChain을 짜놓은 형태가
// 모든 요청이 공통으로 거치는 관문. JWT 필터 삽입 위치·세션 정책·예외 처리가 한눈에 모이는 곳.
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // BCrypt: salt 내장 + 느리게 설계된 해시. password 검증/저장 모두 이 Bean을 통해 간다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager를 Bean으로 꺼내둠 → AuthService가 주입받아 로그인 수행.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // JWT + 무상태라 CSRF 토큰 불필요. 세션 쿠키를 안 쓰므로 CSRF 보호가 의미 없음.
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 기본 인증/폼 로그인 비활성 — 우리가 /auth/login 경로로 직접 JWT 발급.
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                // [핵심] STATELESS: 서버가 세션을 '기억하지' 않음. 매 요청마다 JWT로 재인증.
                // 이 덕분에 수평 확장에 유리하지만, 로그아웃을 서버에서 무효화하려면 별도 블랙리스트 필요.
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(h -> h.frameOptions(f -> f.disable())) // H2 콘솔을 iframe으로 로드하기 위함
                .authorizeHttpRequests(auth -> auth
                        // H2 콘솔 접근 허용. 운영 환경에서는 이 줄부터 제거해야 함.
                        .requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/h2-console/**")).permitAll()
                        // 로그인 자체는 인증 없이 타야 토큰을 발급받을 수 있음.
                        .requestMatchers("/auth/**").permitAll()
                        // [중요] 그 외 전부 인증 필요. @PreAuthorize가 덧붙으면 AND로 추가 판정.
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        // 인증이 아예 없을 때 302(로그인 페이지)로 보내지 말고 그냥 401로 내려준다.
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                // [핵심] 우리 JWT 필터를 UsernamePasswordAuthenticationFilter '앞'에 끼운다.
                // 요청이 컨트롤러(및 @PreAuthorize)에 닿기 전에 SecurityContext가 채워져야
                // 이후 체인이 Principal을 볼 수 있기 때문.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // 개발용 프런트(Vite dev server)에서 부르는 요청을 허용. 5173이 점유되면 Vite가 5174로 폴백하므로 둘 다 허용.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
