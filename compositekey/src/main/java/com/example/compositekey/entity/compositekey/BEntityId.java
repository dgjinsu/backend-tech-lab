package com.example.compositekey.entity.compositekey;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class BEntityId implements Serializable {

    @Column(name = "a_id")
    private String aId;

    @Column(name = "aa_id")
    private String aaId;

    @Column(name = "b_id")
    private String bId;
}

