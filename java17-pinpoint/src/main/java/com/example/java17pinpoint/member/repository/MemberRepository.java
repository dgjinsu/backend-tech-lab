package com.example.java17pinpoint.member.repository;

import com.example.java17pinpoint.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
