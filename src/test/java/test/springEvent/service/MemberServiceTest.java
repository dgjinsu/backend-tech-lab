package test.springEvent.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원 가입 테스트")
    void register() {
        String name = "dgjinsu";

        memberService.register(name);
    }
}