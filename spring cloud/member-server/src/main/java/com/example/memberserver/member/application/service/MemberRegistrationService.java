package com.example.memberserver.member.application.service;

import com.example.common.role.Role;
import com.example.memberserver.member.adaptor.in.web.dto.MemberResponse;
import com.example.memberserver.member.adaptor.in.web.dto.MemberSaveRequest;
import com.example.memberserver.member.adaptor.out.persistence.MemberEntity;
import com.example.memberserver.member.application.port.in.SaveMemberUseCase;
import com.example.memberserver.member.application.port.out.MemberRegistrationPort;
import com.example.memberserver.member.application.service.mapper.MemberDomainMapper;
import com.example.memberserver.member.domain.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberRegistrationService implements SaveMemberUseCase {

    private final MemberDomainMapper mapper;
    private final PasswordEncoder encoder;
    private final MemberRegistrationPort memberRegistrationPort;

    @Override
    public MemberResponse saveMember(MemberSaveRequest request) {
        Member member = mapper.toDomain(request, encoder.encode(request.password()));

        Member savedMember = memberRegistrationPort.saveMember(member);

        return mapper.toMemberResponse(savedMember);
    }
}
