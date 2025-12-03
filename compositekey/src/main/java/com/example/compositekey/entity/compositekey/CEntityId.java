package com.example.compositekey.entity.compositekey;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class CEntityId implements Serializable {

    @Column(name = "a_id")
    private String aId;

    @Column(name = "aa_id")
    private String aaId;

    @Column(name = "c_id")
    private String cId;
}
