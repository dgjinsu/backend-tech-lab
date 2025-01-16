package com.example.java17scouter.member.repository;

import com.example.java17scouter.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
