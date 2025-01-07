package com.example.application.spec;

import com.example.domain.member.Member;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface MemberRepositorySpec {

    void saveMember(Member member);

    List<Member> getMembers();
}
