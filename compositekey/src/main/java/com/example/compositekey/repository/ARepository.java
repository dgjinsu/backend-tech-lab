package com.example.compositekey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.compositekey.entity.AEntity;

@Repository
public interface ARepository extends JpaRepository<AEntity, String> {
}
