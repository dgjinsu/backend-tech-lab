package com.mirero.pwm.member.adapters.persistence.repository;

import com.mirero.pwm.member.adapters.persistence.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

}
