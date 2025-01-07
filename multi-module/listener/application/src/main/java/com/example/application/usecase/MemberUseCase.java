package com.example.application.usecase;

import com.example.application.dto.MemberSaveCommand;
import com.example.domain.member.Member;
import java.util.List;

public interface MemberUseCase {

    void saveMember(MemberSaveCommand command);

    List<Member> getMembers();
}
