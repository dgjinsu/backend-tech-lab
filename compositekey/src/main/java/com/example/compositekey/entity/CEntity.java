package com.example.compositekey.entity;

import com.example.compositekey.entity.compositekey.CEntityId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
    @Column(name = "a_id")
    private String aId;

    @Id
    @Column(name = "aa_id")
    private String aaId;

    @Id
    @Column(name = "c_id")
    private String cId;

    private String cField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "a_id", insertable = false, updatable = false),
        @JoinColumn(name = "aa_id", insertable = false, updatable = false)
    })
    private AEntity aEntity;

    @PrePersist
    public void prePersist() {
        if (this.aEntity != null) {
            this.aId = this.aEntity.getAId();
            this.aaId = this.aEntity.getAaId();
        }
    }
}
