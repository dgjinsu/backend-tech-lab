package com.example.compositekey.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.compositekey.entity.AEntity;
import com.example.compositekey.entity.BEntity;
import com.example.compositekey.entity.CEntity;
import com.example.compositekey.entity.compositekey.BEntityId;
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
        AEntity aEntity = aRepository.save(AEntity.builder().aId("1").aField("aField").build());

//        BEntity bEntity = bRepository.save(
//            BEntity.builder()
//                .id(new BEntityId(aEntity.getAId(), "1"))
//                .bField("bField")
////            .aEntity(aEntity)
//                .build()
//        );

        // C 엔티티 생성 - A와 연관관계를 맺으면서
        CEntity cEntity = cRepository.save(CEntity.builder()
            .aId(aEntity.getAId())
            .cId("C1")
            .cField("cField")
//            .aEntity(aEntity) // MapsId 때문에 연관관계만 설정해도 FK 값 들어감
            .build()
        );

        return "success";
    }
}
