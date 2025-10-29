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
public class AEntityId implements Serializable {
    @Column(name = "a_id")
    private String aId;
    
    @Column(name = "aa_id")
    private String aaId;
}
