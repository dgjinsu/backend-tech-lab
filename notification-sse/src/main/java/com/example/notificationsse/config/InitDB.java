package com.example.notificationsse.config;

import com.example.notificationsse.entity.User;
import com.example.notificationsse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitDB implements CommandLineRunner {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 이미 데이터가 있으면 초기화하지 않음
        if (userRepository.count() > 0) {
            log.info("이미 사용자 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }
        
        log.info("초기 데이터 생성을 시작합니다...");
        
        // 초기 사용자 데이터 생성
        User user1 = new User("user1", "홍길동");
        User user2 = new User("user2", "김철수");
        User user3 = new User("user3", "이영희");
        User user4 = new User("user4", "박민수");
        
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        
        log.info("초기 데이터 생성 완료: 총 {}명의 사용자 생성", userRepository.count());
    }
}

