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
        try {
            bService.saveMemberFail(bMember);
        } catch (RuntimeException e) {
            System.out.println("Required 트랜잭션 롤백 처리: " + e.getMessage());
        }
    }

    @Transactional
    public void saveWithMandatorySuccess(Member aMember, Member bMember) {
        memberRepository.save(aMember);
        bService.saveMemberWithMandatory(bMember);
    }

    public void saveWithMandatoryFail(Member aMember, Member bMember) {
        memberRepository.save(aMember);
        bService.saveMemberWithMandatory(bMember);
    }

    @Transactional
    public void saveWithNotSupported(Member aMember, Member bMember) {
        memberRepository.save(aMember);
        bService.saveWithNotSupported(bMember);
        throw new RuntimeException();
    }

    @Transactional
    public void saveWithNeverFail(Member aMember, Member bMember) {
        memberRepository.save(aMember);
        bService.saveWithNeverFail(bMember);
    }

    @Transactional
    public void saveWithNestedParentException(Member aMember, Member bMember) {
        memberRepository.save(aMember);
        bService.saveWithNestedParentException(bMember);
        throw new RuntimeException();
    }

    @Transactional
    public void saveWithNestedChildException(Member aMember, Member bMember) {
        memberRepository.save(aMember);
        try {
            bService.saveWithNestedChildException(bMember); // 중첩 트랜잭션
        } catch (RuntimeException e) {
            System.out.println("중첩 트랜잭션 롤백 처리: " + e.getMessage());
        }
    }
}
