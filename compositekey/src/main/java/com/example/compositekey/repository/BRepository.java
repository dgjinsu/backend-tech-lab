package com.example.compositekey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.compositekey.entity.BEntity;
import com.example.compositekey.entity.compositekey.BEntityId;

@Repository
public interface BRepository extends JpaRepository<BEntity, BEntityId> {
}
