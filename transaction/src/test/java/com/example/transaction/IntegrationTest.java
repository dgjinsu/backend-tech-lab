package com.example.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.transaction.entity.Member;
import com.example.transaction.repository.MemberRepository;
import com.example.transaction.service.AService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.TransactionSystemException;

@SpringBootTest
class IntegrationTest {

    @Autowired
    private AService aService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("[REQUIRED] 저장 성공 테스트")
    public void saveWithRequiredSuccessTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        aService.saveWithRequiredSuccess(aMember, bMember);

        assertThat(memberRepository.findAll()).size().isEqualTo(2);
    }

    @Test
    @DisplayName("[REQUIRED] 자식에서 예외가 터졌을 때")
    public void saveWithRequiredFailTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        assertThatThrownBy(() -> aService.saveWithRequiredFail(aMember, bMember))
            .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("[MANDATORY] 저장 성공 테스트")
    public void saveWithMandatorySuccessTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        aService.saveWithMandatorySuccess(aMember, bMember);

        assertThat(memberRepository.findAll()).size().isEqualTo(2);
    }

    @Test
    @DisplayName("[MANDATORY] 부모가 트랜잭션이 없을 때")
    public void saveWithMandatoryFailTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        // 부모에서 트랜잭션 없이 실행
        assertThatThrownBy(() -> aService.saveWithMandatoryFail(aMember, bMember))
            .isInstanceOf(IllegalTransactionStateException.class);
    }

    @Test
    @DisplayName("[NOT SUPPORTED] 자식은 Not Supported 이고, 부모에서 예외가 발생할 때")
    public void saveWithNotSupportedTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        assertThatThrownBy(() -> aService.saveWithNotSupported(aMember, bMember))
            .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.findAll()).size().isEqualTo(1);
    }

    @Test
    @DisplayName("[NEVER] 자식은 Never 이고, 부모가 트랜잭션이 있을 때")
    public void saveWithNeverFailTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        assertThatThrownBy(() -> aService.saveWithNeverFail(aMember, bMember))
            .isInstanceOf(IllegalTransactionStateException.class);
    }

    @Test
    @DisplayName("[NESTED] 부모 트랜잭션의 롤백이 자식에게 전파")
    public void saveWithNestedParentExceptionTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        assertThatThrownBy(() -> aService.saveWithNestedParentException(aMember, bMember))
            .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.findAll()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("[NESTED] 자식 트랜잭션의 롤백이 부모에게 전파되지 않음")
    public void saveWithNestedChildExceptionTest() {
        Member aMember = new Member(1L);
        Member bMember = new Member(2L);

        aService.saveWithNestedChildException(aMember, bMember);

        assertThat(memberRepository.findAll()).size().isEqualTo(1);
    }

}