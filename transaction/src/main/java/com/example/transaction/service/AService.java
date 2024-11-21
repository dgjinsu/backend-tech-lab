package com.example.transaction.service;

import com.example.transaction.entity.Member;
import com.example.transaction.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AService {

    private final MemberRepository memberRepository;
    private final BService bService;

    @Transactional
    public void saveWithRequiredSuccess(Member aMember, Member bMember) {
        memberRepository.save(aMember);
        bService.saveMemberSuccess(bMember);
    }

    @Transactional
    public void saveWithRequiredFail(Member aMember, Member bMember) {
        memberRepository.save(aMember);
        // 예외 발생
        bService.saveMember(bMember);
    }

    @Transactional
    public void saveWithMandatorySuccess(Member aMember, Member bMember) {
        memberRepository.save(aMember);
        bService.saveMemberWithMandatory(bMember);
    }

    public void saveWithMandatoryFail(Member aMember, Member bMember) {
        memberRepository.save(aMember);
        bService.saveMemberSuccess(bMember);
    }
}
