package com.example.memberserver.member.application.service;

import com.example.memberserver.member.adaptor.in.web.dto.MemberInfoResponse;
import com.example.memberserver.member.application.port.in.GetMemberInfoQuery;
import com.example.memberserver.member.application.port.out.MemberInfoPort;
import com.example.memberserver.member.application.service.mapper.MemberDomainMapper;
import com.example.memberserver.member.domain.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberInfoService implements GetMemberInfoQuery {

    private final MemberDomainMapper mapper;
    private final MemberInfoPort memberInfoPort;
    @Override
    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = memberInfoPort.findById(memberId);

        return mapper.toMemberInfoResponse(member);
    }
}
