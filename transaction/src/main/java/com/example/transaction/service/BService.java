package com.example.transaction.service;

import com.example.transaction.entity.Member;
import com.example.transaction.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BService {
    private final MemberRepository memberRepository;

    @Transactional
    public void saveMemberFail(Member bMember) {
        memberRepository.save(bMember);
        throw new RuntimeException();
    }

    @Transactional
    public void saveMemberSuccess(Member bMember) {
        memberRepository.save(bMember);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveMemberWithMandatory(Member bMember) {
        memberRepository.save(bMember);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void saveWithNotSupported(Member bMember) {
        memberRepository.save(bMember);
    }

    @Transactional(propagation = Propagation.NEVER)
    public void saveWithNeverFail(Member bMember) {
        memberRepository.save(bMember);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void saveWithNestedParentException(Member bMember) {
        memberRepository.save(bMember);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void saveWithNestedChildException(Member bMember) {
        memberRepository.save(bMember);
        throw new RuntimeException();
    }
}
