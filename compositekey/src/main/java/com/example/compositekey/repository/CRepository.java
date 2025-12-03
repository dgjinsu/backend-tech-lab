package com.example.compositekey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.compositekey.entity.CEntity;
import com.example.compositekey.entity.compositekey.CEntityId;

@Repository
public interface CRepository extends JpaRepository<CEntity, CEntityId> {
}
