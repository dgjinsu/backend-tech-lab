package com.example.archunittest.member.persistence.repository;

import com.example.archunittest.member.persistence.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

}
