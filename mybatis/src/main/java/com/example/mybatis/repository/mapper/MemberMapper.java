package com.example.mybatis.repository.mapper;

import com.example.mybatis.dto.MemberResponse;
import com.example.mybatis.dto.SaveMemberRequest;
import com.example.mybatis.dto.UpdateMemberRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    void saveMember(SaveMemberRequest request);

    MemberResponse findById(Long memberId);

    void updateMemberInfo(Long memberId, UpdateMemberRequest request);
}
