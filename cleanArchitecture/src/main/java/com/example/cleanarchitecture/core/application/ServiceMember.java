package com.example.cleanarchitecture.core.application;

import com.example.cleanarchitecture.core.domain.Member;
import com.example.cleanarchitecture.core.dto.command.SaveMemberCommand;
import com.example.cleanarchitecture.core.dto.query.MemberListQuery;
import com.example.cleanarchitecture.core.dto.query.MemberQuery;
import com.example.cleanarchitecture.core.spec.MemberSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiceMember implements SaveMemberUseCase, GetMemberUseCase {
    private final MemberSpec memberSpec;

    @Override
    public MemberQuery saveMember(SaveMemberCommand command) {
        return memberSpec.saveMember(new Member(command.loginId(), command.password()));
    }

    @Override
    public MemberListQuery getAllMembers() {
        return memberSpec.findAllMembers();
    }
}
