package com.example.lisalgorithmforfilmlayer.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 하나의 diff 연산. {@code note}에 사람이 읽을 한 줄 설명을 담아 화면에서 그대로 출력한다.
 *
 * @param fromIndex 원래(before) 위치 — DELETE/MOVE에서 사용, 아니면 null
 * @param toIndex   목표(after) 위치 — INSERT/MOVE/UPDATE에서 사용, 아니면 null
 */
public record DiffOp(OpType type, String id, String model,
                     Integer fromIndex, Integer toIndex, String note) {

    /** 두께/거칠기 표시용: 정수면 소수점 제거(20.0 → 20). */
    static String fmt(double d) {
        return (d == Math.rint(d) && !Double.isInfinite(d)) ? Long.toString((long) d) : Double.toString(d);
    }

    // ── §4.1 삭제 ────────────────────────────────────────────────
    public static DiffOp delete(Layer l, int fromIndex) {
        return new DiffOp(OpType.DELETE, l.id(), l.model(), fromIndex, null,
                "삭제  [%d] %s".formatted(fromIndex, l.model()));
    }

    // ── §4.1 생성 ────────────────────────────────────────────────
    public static DiffOp insert(Layer l, int toIndex) {
        return new DiffOp(OpType.INSERT, l.id(), l.model(), null, toIndex,
                "생성  [%d] %s (두께 %s, 거칠기 %s)".formatted(toIndex, l.model(), fmt(l.thickness()), fmt(l.roughness())));
    }

    // ── §4.2 이동 (LIS 앵커에서 벗어난 공통 레이어) ────────────────
    public static DiffOp move(Layer l, int fromIndex, int toIndex) {
        return new DiffOp(OpType.MOVE, l.id(), l.model(), fromIndex, toIndex,
                "이동  %s : 위치 %d → %d".formatted(l.model(), fromIndex, toIndex));
    }

    // ── §4.3 수정 (같은 id, 내용만 다름) ──────────────────────────
    public static DiffOp update(Layer before, Layer after, int atIndex) {
        List<String> ch = new ArrayList<>();
        if (!Objects.equals(before.model(), after.model()))
            ch.add("model %s→%s".formatted(before.model(), after.model()));
        if (before.thickness() != after.thickness())
            ch.add("두께 %s→%s".formatted(fmt(before.thickness()), fmt(after.thickness())));
        if (before.roughness() != after.roughness())
            ch.add("거칠기 %s→%s".formatted(fmt(before.roughness()), fmt(after.roughness())));
        return new DiffOp(OpType.UPDATE, after.id(), after.model(), atIndex, atIndex,
                "수정  [%d] %s : %s".formatted(atIndex, after.model(), String.join(", ", ch)));
    }

    // ── §6 실행 계획: MOVE를 삭제+재삽입으로 낮춘 형태 ─────────────
    public static DiffOp moveRemoval(DiffOp mv) {
        return new DiffOp(OpType.DELETE, mv.id(), mv.model(), mv.fromIndex(), null,
                "이동-제거   %s @%d".formatted(mv.model(), mv.fromIndex()));
    }

    public static DiffOp moveInsertion(DiffOp mv) {
        return new DiffOp(OpType.INSERT, mv.id(), mv.model(), null, mv.toIndex(),
                "이동-재삽입 %s @%d".formatted(mv.model(), mv.toIndex()));
    }
}
