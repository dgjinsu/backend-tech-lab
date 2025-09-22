package com.example.compositekey.entity.compositekey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class EEntityId implements Serializable {

    private BEntityId bEntity; // BEntity의 복합키를 직접 참조
    private String eId;
}
