package com.example.lisalgorithmforfilmlayer.diff;

/**
 * "after 스택 분석" 표의 한 행 (RFC §4.4 워크스루 재현 + 생성 레이어 포함).
 *
 * <p>after 순서 그대로 한 줄씩. 공통 레이어는 before 위치번호로 치환해 LIS를 태우고,
 * 생성(신규) 레이어는 before가 없어 수열에서 빠진다.</p>
 *
 * @param afterIndex after 위치번호
 * @param model      레이어 모델명
 * @param beforePos  before 위치번호 (생성 레이어면 null)
 * @param lisLen     "여기서 끝나는 최장 증가 길이 L" (생성 레이어면 null)
 * @param state      "anchor"(유지) / "move"(이동) / "insert"(생성)
 */
public record LisRow(int afterIndex, String model, Integer beforePos, Integer lisLen, String state) {
}
