package com.example.lisalgorithmforfilmlayer.diff;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 스냅샷 diff — before[] 와 after[] 를 비교해 최소 연산(삭제·생성·이동·수정)을 계산한다.
 * (RFC "FILM 레이어 증분 편집" §4~§6 구현)
 *
 * <p>배열 순서 규약: index 0 = Baseline(맨 아래), 위로 갈수록 증가. before/after 동일 규약.</p>
 */
@Service
public class LayerDiffService {

    public DiffResult diff(List<Layer> before, List<Layer> after) {

        // id → before 위치 / id → 레이어 조회 테이블
        Map<String, Integer> beforePos = new HashMap<>();
        Map<String, Layer> beforeById = new HashMap<>();
        for (int i = 0; i < before.size(); i++) {
            beforePos.put(before.get(i).id(), i);
            beforeById.put(before.get(i).id(), before.get(i));
        }
        Map<String, Layer> afterById = new HashMap<>();
        Set<String> afterIds = new HashSet<>();
        for (Layer l : after) {
            afterById.put(l.id(), l);
            afterIds.add(l.id());
        }

        // ── §4.1 삭제 = before에 있고 after에 없는 id ────────────────────────
        List<DiffOp> deletes = new ArrayList<>();
        for (int i = 0; i < before.size(); i++) {
            Layer l = before.get(i);
            if (!afterIds.contains(l.id())) deletes.add(DiffOp.delete(l, i));
        }

        // ── §4.1 생성 = after에 있고 before에 없는 id ────────────────────────
        List<DiffOp> inserts = new ArrayList<>();
        for (int j = 0; j < after.size(); j++) {
            Layer l = after.get(j);
            if (!beforePos.containsKey(l.id())) inserts.add(DiffOp.insert(l, j));
        }

        // ── §4.2 이동 = LIS 앵커 ─────────────────────────────────────────────
        // 공통 레이어(삭제·생성 제외)를 after 순서대로 늘어놓고 각자의 before 위치번호로 치환한다.
        List<Integer> commonAfterIdx = new ArrayList<>(); // 공통 레이어의 after 위치
        List<Integer> seqList = new ArrayList<>();         // 그 레이어의 before 위치 (= LIS를 태울 수열)
        for (int j = 0; j < after.size(); j++) {
            String id = after.get(j).id();
            if (beforePos.containsKey(id)) {
                commonAfterIdx.add(j);
                seqList.add(beforePos.get(id));
            }
        }
        int m = seqList.size();
        int[] seq = seqList.stream().mapToInt(Integer::intValue).toArray();

        // "왼→오로 갈수록 커지는 최장 무리(LIS)" = 원래 상대순서를 유지한 = 안 움직인 앵커.
        // O(m^2)로 각 원소에서 끝나는 LIS 길이 L[]과 역추적 링크 prev[]를 함께 구해 §4.4 표를 그대로 재현한다.
        int[] len = new int[m];
        int[] prev = new int[m];
        int bestEnd = -1;
        for (int i = 0; i < m; i++) {
            len[i] = 1;
            prev[i] = -1;
            for (int k = 0; k < i; k++) {
                if (seq[k] < seq[i] && len[k] + 1 > len[i]) { // 엄격 증가
                    len[i] = len[k] + 1;
                    prev[i] = k;
                }
            }
            if (bestEnd == -1 || len[i] > len[bestEnd]) bestEnd = i;
        }
        Set<Integer> anchorIdx = new HashSet<>(); // 공통 리스트 상의 인덱스
        for (int k = bestEnd; k != -1; k = prev[k]) anchorIdx.add(k);

        // after 스택 분석 표 — after 순서 그대로. 공통은 앵커/이동, 신규는 생성(before 없음).
        Map<Integer, Integer> afterIdxToCommon = new HashMap<>();
        for (int i = 0; i < m; i++) afterIdxToCommon.put(commonAfterIdx.get(i), i);

        List<LisRow> lisTable = new ArrayList<>();
        for (int j = 0; j < after.size(); j++) {
            Layer l = after.get(j);
            Integer ci = afterIdxToCommon.get(j);
            if (ci != null) {
                lisTable.add(new LisRow(j, l.model(), seq[ci], len[ci], anchorIdx.contains(ci) ? "anchor" : "move"));
            } else {
                lisTable.add(new LisRow(j, l.model(), null, null, "insert"));
            }
        }

        // 삭제된 레이어 — after에 없으므로 표에서 빼고 따로 보여준다 (before 위치 순).
        List<DelRow> deletedRows = new ArrayList<>();
        for (DiffOp d : deletes) deletedRows.add(new DelRow(d.fromIndex(), d.model()));

        // 앵커 밖 공통 레이어 = 이동
        List<DiffOp> moves = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            if (!anchorIdx.contains(i)) {
                int aidx = commonAfterIdx.get(i);
                Layer l = after.get(aidx);
                moves.add(DiffOp.move(l, beforePos.get(l.id()), aidx));
            }
        }

        // ── §4.3 수정 = 앵커(이동 없음)인데 내용만 다른 것 ───────────────────
        // (이동하는 레이어는 실행계획에서 after 내용으로 재삽입되므로 별도 UPDATE가 필요 없다)
        List<DiffOp> updates = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            if (anchorIdx.contains(i)) {
                int aidx = commonAfterIdx.get(i);
                Layer aft = after.get(aidx);
                Layer bef = beforeById.get(aft.id());
                if (!bef.sameContent(aft)) updates.add(DiffOp.update(bef, aft, aidx));
            }
        }

        // ── §6 실행 계획 = 삭제 desc → 삽입 asc → 수정 (MOVE = 삭제 + 재삽입) ──
        List<DiffOp> removals = new ArrayList<>(deletes);
        for (DiffOp mv : moves) removals.add(DiffOp.moveRemoval(mv));
        removals.sort(Comparator.comparingInt(DiffOp::fromIndex).reversed()); // 높은 index부터

        List<DiffOp> insertions = new ArrayList<>(inserts);
        for (DiffOp mv : moves) insertions.add(DiffOp.moveInsertion(mv));
        insertions.sort(Comparator.comparingInt(DiffOp::toIndex));            // target 오름차순

        List<DiffOp> executionPlan = new ArrayList<>();
        executionPlan.addAll(removals);
        executionPlan.addAll(insertions);
        executionPlan.addAll(updates);

        // ── §5.1 메모리 사전검증: 계산한 op을 before에 순수 적용 → == after ? ─
        StringBuilder log = new StringBuilder();
        boolean verified = simulate(before, after, afterById, removals, insertions, updates, log);

        Summary summary = new Summary(before.size(), after.size(),
                deletes.size(), inserts.size(), moves.size(), updates.size(), m, anchorIdx.size());

        // 화면 표시는 스택 그림처럼 layer 0(Baseline)이 맨 아래 오도록 뒤집는다. (행의 번호는 그대로)
        Collections.reverse(lisTable);
        Collections.reverse(deletedRows);

        return new DiffResult(before, after, lisTable, deletedRows, deletes, inserts, moves, updates,
                executionPlan, verified, log.toString(), summary);
    }

    /**
     * §5.1 파괴적 조작 전 게이트 — 실행계획을 메모리 리스트에 그대로 적용하고 after와 비교한다.
     * 삭제(내림차순)로 좌표가 밀리지 않고, 삽입(오름차순)이 target 좌표를 자연히 맞춘다는 §6 규칙을
     * 실제로 시뮬레이션한다.
     */
    private boolean simulate(List<Layer> before, List<Layer> after, Map<String, Layer> afterById,
                             List<DiffOp> removals, List<DiffOp> insertions, List<DiffOp> updates,
                             StringBuilder log) {
        List<Layer> work = new ArrayList<>(before);
        log.append("시작            : ").append(models(work)).append('\n');

        for (DiffOp r : removals) {
            work.removeIf(l -> l.id().equals(r.id()));
            log.append("제거 @%-2d %-5s : ".formatted(r.fromIndex(), r.model())).append(models(work)).append('\n');
        }
        for (DiffOp u : updates) {
            for (int i = 0; i < work.size(); i++)
                if (work.get(i).id().equals(u.id())) work.set(i, afterById.get(u.id()));
            log.append("수정     %-5s : ".formatted(u.model())).append(models(work)).append('\n');
        }
        for (DiffOp ins : insertions) {
            int at = Math.min(ins.toIndex(), work.size());
            work.add(at, afterById.get(ins.id()));
            log.append("삽입 @%-2d %-5s : ".formatted(ins.toIndex(), ins.model())).append(models(work)).append('\n');
        }

        boolean ok = work.equals(after); // record equals → id+model+두께+거칠기 전부 비교
        log.append(ok ? "검증 OK ✅       : " : "검증 실패 ❌      : ").append("기대 ").append(models(after));
        return ok;
    }

    /** 로그용: 모델명만 뽑아 [A, B, C] 형태로. */
    private static String models(List<Layer> ls) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ls.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(ls.get(i).model());
        }
        return sb.append(']').toString();
    }
}
