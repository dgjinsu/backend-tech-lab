package com.example.abac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
// BaseTimeEntity의 @CreatedDate / @LastModifiedDate가 실제로 채워지려면 이 애너테이션 필요.
@EnableJpaAuditing
// [권한 강제 스위치] 이게 없으면 @PreAuthorize("@expensePolicy.canApprove(...)") 식이
// 무시돼서 컨트롤러에 그냥 들어와 버린다. ABAC 판정의 근거가 되는 스위치.
@EnableMethodSecurity
public class AbacApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbacApplication.class, args);
    }

}
