package com.example.archunittest.member.persistence.repository;

import com.example.archunittest.member.application.spec.MemberRepositorySpec;
import com.example.archunittest.member.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long>, MemberRepositorySpec {

    @Override
    void saveMember(MemberEntity memberEntity);
}
