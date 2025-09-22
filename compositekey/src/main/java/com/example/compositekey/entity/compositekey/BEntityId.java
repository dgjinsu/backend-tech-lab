package com.example.compositekey.entity.compositekey;

import java.io.Serializable;

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

    private String aId;
    private String bId;

    public BEntityId(String bId) {
        this.bId = bId;
    }
}
