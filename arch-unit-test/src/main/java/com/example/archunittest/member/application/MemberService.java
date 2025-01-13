package com.example.archunittest.member.application;

import com.example.archunittest.member.application.dto.MemberSaveCommand;
import com.example.archunittest.member.application.dto.MemberSaveQuery;
import com.example.archunittest.member.application.spec.KafkaSpec;
import com.example.archunittest.member.application.spec.MemberRepositorySpec;
import com.example.archunittest.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

    private final MemberRepositorySpec memberRepositorySpec;
    private final KafkaSpec kafkaSpec;

    public MemberSaveQuery save(MemberSaveCommand command) {
        Member member = memberRepositorySpec.saveMember(Member.builder().name("name").build());
        kafkaSpec.sendTest("test");
        return new MemberSaveQuery(member.getId(), member.getName());
    }

}
