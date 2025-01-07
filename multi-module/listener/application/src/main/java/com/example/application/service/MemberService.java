package com.example.application.service;

import com.example.application.dto.MemberSaveCommand;
import com.example.application.spec.MemberRepositorySpec;
import com.example.application.usecase.MemberUseCase;
import com.example.domain.member.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

    private final MemberRepositorySpec memberRepositorySpec;

    @Override
    public void saveMember(MemberSaveCommand command) {
        memberRepositorySpec.saveMember(
            Member.builder()
                .loginId(command.loginId())
                .password(command.password())
                .build()
        );
    }

    @Override
    public List<Member> getMembers() {
        return memberRepositorySpec.getMembers();
    }
}
