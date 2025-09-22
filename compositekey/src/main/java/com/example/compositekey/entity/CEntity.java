package com.example.compositekey.entity;

import com.example.compositekey.entity.compositekey.CEntityId;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "C")
@IdClass(CEntityId.class)
public class CEntity {

    @Id
    private String aId;

    @Id
    private String cId;

    private String cField;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("aId")
    @JoinColumn(name = "a_id")
    private AEntity aEntity;
}
