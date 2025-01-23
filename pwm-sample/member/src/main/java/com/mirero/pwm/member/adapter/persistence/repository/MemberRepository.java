package com.mirero.pwm.member.adapter.persistence.repository;

import com.mirero.pwm.member.adapter.persistence.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

}
