package com.example.compositekey.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.compositekey.entity.AEntity;
import com.example.compositekey.entity.BEntity;
import com.example.compositekey.entity.CEntity;
import com.example.compositekey.repository.ARepository;
import com.example.compositekey.repository.BRepository;
import com.example.compositekey.repository.CRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final ARepository aRepository;
    private final BRepository bRepository;
    private final CRepository cRepository;

    @GetMapping("/test")
    public String test() {
        // A 엔티티 저장
        AEntity aEntity = aRepository.save(AEntity.builder()
            .aId("1")
            .aaId("2")
            .aField("aField")
            .build());

        // C 엔티티 생성 - cId만 지정하고 aEntity 연관관계만 설정
        // @PrePersist에서 자동으로 aId, aaId가 설정됨
        CEntity cEntity = cRepository.save(CEntity.builder()
            .cId("C1")
            .cField("cField")
            .aEntity(aEntity)
            .build());

        return "C Entity saved: aId=" + cEntity.getAId() + 
               ", aaId=" + cEntity.getAaId() + 
               ", cId=" + cEntity.getCId();
    }
}
