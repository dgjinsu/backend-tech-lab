package com.mirero.pwm.member.persistence.repository;

import com.mirero.pwm.member.persistence.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

}
