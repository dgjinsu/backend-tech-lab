package com.example.lisalgorithmforfilmlayer.diff;

import java.util.Objects;

/**
 * FILM 레이어 한 장. (RFC §3 — id 있는 레이어)
 *
 * <p>{@code id}는 diff 계산용 내부 식별자다. before의 어떤 레이어가 after의 어떤
 * 레이어와 "같은 것"인지 이어야 <b>밀린 것</b>과 <b>진짜 이동</b>을 구분할 수 있다(§4.2).
 * OLSA에는 저장하지 않고 편집 세션 모델에만 존재한다.</p>
 *
 * <p>배열 순서 규약: index 0 = Baseline(맨 아래), 위로 갈수록 index 증가.</p>
 */
public record Layer(String id, String model, double thickness, double roughness) {

    /** 이동과 무관하게 "내용(값)"이 같은지 — §4.3 UPDATE 판정용. */
    public boolean sameContent(Layer o) {
        return Objects.equals(model, o.model)
                && thickness == o.thickness
                && roughness == o.roughness;
    }
}
