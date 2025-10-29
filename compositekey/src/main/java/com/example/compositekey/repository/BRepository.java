package com.example.compositekey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.compositekey.entity.BEntity;
import com.example.compositekey.entity.compositekey.BEntityId;

public interface BRepository extends JpaRepository<BEntity, BEntityId> {

}

