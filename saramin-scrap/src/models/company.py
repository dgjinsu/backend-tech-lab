from dataclasses import dataclass, asdict
from datetime import datetime
from typing import Optional

@dataclass
class Company:
    """회사 정보 데이터 클래스"""
    name: str                      # 회사 이름 (필수)
    employee_count: str = '-'      # 인원 수 (기본값: '-')
    location: str = '-'            # 위치 (기본값: '-')
    salary: str = '-'              # 매출액 (기본값: '-')
    source_url: str = ''           # 출처 URL
    scraped_at: Optional[str] = None  # 크롤링 시각

    def __post_init__(self):
        """초기화 후 처리"""
        if self.scraped_at is None:
            self.scraped_at = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

    def to_dict(self) -> dict:
        """딕셔너리로 변환"""
        return asdict(self)
