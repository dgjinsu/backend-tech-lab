package com.example.compositekey.entity;

import com.example.compositekey.entity.compositekey.BEntityId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
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
@Table(name = "B")
public class BEntity {

    @EmbeddedId
    private BEntityId id;

    private String bField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "a_id", referencedColumnName = "a_id", insertable = false, updatable = false),
        @JoinColumn(name = "aa_id", referencedColumnName = "aa_id", insertable = false, updatable = false)
    })
    private AEntity aEntity;
}

