package com.example.lisalgorithmforfilmlayer.diff;

/**
 * 삭제된 레이어 한 행. after에 없어 LIS 수열에 끼면 헷갈리므로 표에서 분리해 따로 보여준다.
 *
 * @param beforePos before 위치번호 (원래 있던 자리)
 * @param model     레이어 모델명
 */
public record DelRow(int beforePos, String model) {
}
