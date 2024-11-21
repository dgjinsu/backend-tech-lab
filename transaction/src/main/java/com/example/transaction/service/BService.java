package com.example.transaction.service;

import com.example.transaction.entity.Member;
import com.example.transaction.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BService {
    private final MemberRepository memberRepository;

    public void saveMember(Member bMember) {
        memberRepository.save(bMember);
        throw new RuntimeException();
    }

    public void saveMemberSuccess(Member bMember) {
        memberRepository.save(bMember);
    }
}
