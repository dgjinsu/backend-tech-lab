package com.mirero.pwm.member.application.usecase.member.queries;

import com.mirero.pwm.member.application.usecase.member.GetMemberInfoRepoPort;
import com.mirero.pwm.member.application.usecase.member.queries.dto.GetMemberDto;
import com.mirero.pwm.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetMemberUseCase  implements GetMemberQuery{

    private final GetMemberInfoRepoPort getMemberInfoRepoPort;

    @Override
    public GetMemberDto getMemberInfo(Long memberId) {
        Member member = getMemberInfoRepoPort.getMemberInfo(memberId);
        return GetMemberDto.from(member);
    }
}
