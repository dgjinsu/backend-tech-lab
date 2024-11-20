package com.example.memberserver.member.adaptor.out.mapper;

import com.example.memberserver.member.adaptor.out.persistence.MemberEntity;
import com.example.memberserver.member.domain.Member;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberEntityMapper {
    MemberEntityMapper INSTANCE = Mappers.getMapper(MemberEntityMapper.class);

    MemberEntity toMemberEntity(Member member);

    Member toMember(MemberEntity memberEntity);
}
