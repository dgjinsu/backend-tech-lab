package com.example.cleanarchitecture.infrastructures.persistence.repository;

import com.example.cleanarchitecture.infrastructures.persistence.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

}
