package com.example.mybatis.repository;

import com.example.mybatis.dto.MemberResponse;
import com.example.mybatis.dto.SaveMemberRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    void saveMember(SaveMemberRequest request);

    MemberResponse findById(Long memberId);
}
