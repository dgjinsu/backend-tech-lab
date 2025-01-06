package com.example.archunittest.member.application;

import com.example.archunittest.member.application.dto.MemberSaveCommand;
import com.example.archunittest.member.application.spec.MemberRepositorySpec;
import com.example.archunittest.member.domain.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

    private final MemberRepositorySpec memberRepositorySpec;

    public void save(MemberSaveCommand command) {
        memberRepositorySpec.saveMember(new MemberEntity());
    }

}
