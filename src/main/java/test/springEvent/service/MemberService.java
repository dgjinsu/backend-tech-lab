package test.springEvent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import test.springEvent.entity.Member;
import test.springEvent.event.RegisteredEvent;
import test.springEvent.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final ApplicationEventPublisher publisher;
    private final MemberRepository memberRepository;

    @Transactional
    public void register(String name) throws Exception {
        // 회원가입 처리 로직
        memberRepository.save(Member.builder().name(name).build());

        // 이벤트 등록
        publisher.publishEvent(new RegisteredEvent(name));
    }
}