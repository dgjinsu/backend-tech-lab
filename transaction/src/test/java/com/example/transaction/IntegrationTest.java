package com.example.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.transaction.entity.Member;
import com.example.transaction.repository.MemberRepository;
import com.example.transaction.service.AService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;

@SpringBootTest
class IntegrationTest {

    @Autowired
    private AService aService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void saveWithRequiredSuccessTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        aService.saveWithRequiredSuccess(aMember, bMember);

        assertThat(memberRepository.findAll()).size().isEqualTo(2);
    }

    @Test
    public void saveWithRequiredFailTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        assertThatThrownBy(() -> aService.saveWithRequiredFail(aMember, bMember))
            .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.findAll()).isEmpty();
    }

    @Test
    public void saveWithMandatorySuccessTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        aService.saveWithMandatorySuccess(aMember, bMember);

        assertThat(memberRepository.findAll()).size().isEqualTo(2);
    }

    @Test
    public void saveWithMandatoryFailTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        aService.saveWithMandatoryFail(aMember, bMember);

//        // 부모에서 트랜잭션 없이 실행
//        assertThatThrownBy(() -> aService.saveWithMandatoryFail(aMember, bMember))
//            .isInstanceOf(TransactionSystemException.class);
    }

}