package com.example.compositekey.entity;

import com.example.compositekey.entity.compositekey.AEntityId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@IdClass(AEntityId.class)
@Table(name = "A")
public class AEntity {

    @Id
    @Column(name = "a_id")
    String aId;

    @Id
    @Column(name = "aa_id")
    String aaId;

    String aField;
}
