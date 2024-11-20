package com.example.memberserver.member.application.service.mapper;

import com.example.memberserver.member.adaptor.in.web.dto.MemberInfoResponse;
import com.example.memberserver.member.adaptor.in.web.dto.MemberResponse;
import com.example.memberserver.member.adaptor.in.web.dto.MemberSaveRequest;
import com.example.memberserver.member.domain.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberDomainMapper {

    MemberDomainMapper INSTANCE = Mappers.getMapper(MemberDomainMapper.class);

    Member toDomain(MemberSaveRequest request, String password);

    @Mapping(source = "id", target = "memberId")
    MemberResponse toMemberResponse(Member member);

    @Mapping(source = "id", target = "memberId")
    MemberInfoResponse toMemberInfoResponse(Member member);
}
