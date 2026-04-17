package com.example.abac.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 인증 진입점: CustomUserDetailsService.loadUserByUsername()에서 username으로 User를 찾고
    // CustomUserPrincipal로 감싼다. username은 User 엔티티에서 unique 컬럼이라 단건 조회 OK.
    Optional<User> findByUsername(String username);
}
