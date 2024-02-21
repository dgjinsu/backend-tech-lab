package test.springEvent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final ApplicationEventPublisher publisher;
    private final PlatformTransactionManager transactionManager;
    private final TestService testService;

    public void register(String name) {
        // 프로그래밍 방식으로 트랜잭션 정의
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        // 트랜잭션 상태 가져오기
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            // 회원가입 처리 로직
//            System.out.println("회원 추가 완료");

            // 이벤트 등록
//            publisher.publishEvent(new RegisteredEvent(name));

            testService.a();

            // 트랜잭션 커밋
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            System.out.println("트랜잭션 롤백");
        }
    }
}