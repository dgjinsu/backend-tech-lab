import pandas as pd
from typing import List
from pathlib import Path
import logging

logger = logging.getLogger(__name__)

def export_to_csv(companies: List[dict], output_path: str):
    """회사 데이터를 CSV 파일로 저장

    Args:
        companies: Company 객체의 딕셔너리 리스트
        output_path: 저장할 CSV 파일 경로
    """
    if not companies:
        logger.warning("저장할 데이터가 없습니다")
        return

    # DataFrame 생성
    df = pd.DataFrame(companies)

    # 컬럼 순서 정리
    columns = ['name', 'employee_count', 'location', 'salary', 'source_url', 'scraped_at']
    df = df[columns]

    # 컬럼명 한글로 변경
    df.columns = ['회사명', '인원수', '위치', '매출액', '출처URL', '크롤링시각']

    # CSV 저장 (UTF-8 BOM으로 저장하여 엑셀에서 한글 깨짐 방지)
    output_path = Path(output_path)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    df.to_csv(output_path, index=False, encoding='utf-8-sig')

    logger.info(f"✅ CSV 저장 완료: {output_path} ({len(companies)}개 회사)")
