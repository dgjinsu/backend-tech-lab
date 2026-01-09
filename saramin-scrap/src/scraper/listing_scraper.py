from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
from typing import List
import logging
import time

logger = logging.getLogger(__name__)

class ListingScraper:
    """채용 공고 리스트 페이지 스크래퍼"""

    def __init__(self, driver):
        self.driver = driver

    def scrape_listing_page(self, base_url: str, page: int) -> List[str]:
        """특정 페이지의 공고 리스트에서 회사 URL 추출

        Args:
            base_url: 기본 URL (페이지 파라미터 제외)
            page: 페이지 번호

        Returns:
            회사 상세 페이지 URL 리스트
        """
        # URL 생성 (page 파라미터 업데이트)
        url = self._build_url(base_url, page)
        logger.info(f"📄 페이지 {page} 크롤링 시작: {url}")

        try:
            # 페이지 로드
            self.driver.get(url)

            # 페이지 로딩 대기 (더 긴 시간)
            time.sleep(3)

            # 페이지 소스 파싱
            soup = BeautifulSoup(self.driver.page_source, 'lxml')

            # 디버그: HTML 일부 저장
            logger.debug(f"페이지 HTML 길이: {len(soup.text)}")

            # 회사 URL 추출
            company_urls = self._extract_company_urls(soup)

            logger.info(f"✅ 페이지 {page}: {len(company_urls)}개 회사 URL 추출")
            return company_urls

        except Exception as e:
            logger.error(f"❌ 페이지 {page} 크롤링 실패: {e}")
            return []

    def _build_url(self, base_url: str, page: int) -> str:
        """페이지 번호가 포함된 URL 생성"""
        if 'page=' in base_url:
            # 기존 page 파라미터 교체
            import re
            return re.sub(r'page=\d+', f'page={page}', base_url)
        else:
            # page 파라미터 추가
            separator = '&' if '?' in base_url else '?'
            return f"{base_url}{separator}page={page}"

    def _extract_company_urls(self, soup: BeautifulSoup) -> List[str]:
        """공고 리스트에서 회사 내부 채용 URL 추출

        실제 흐름:
        1. 공고 리스트 → view-inner-recruit URL 추출
        2. 나중에 CompanyScraper에서 view-inner-recruit → view?csn으로 변환
        """
        urls = []
        from src.config import SARAMIN_BASE_URL

        # a.str_tit 링크 찾기 (회사 이름 링크)
        company_links = soup.select('a.str_tit[href*="company-info/view-inner-recruit"]')

        logger.info(f"✅ {len(company_links)}개 회사 링크 발견")

        for link in company_links:
            href = link.get('href')
            company_name = link.get_text(strip=True)

            if href:
                # 상대 URL을 절대 URL로 변환
                if href.startswith('/'):
                    href = SARAMIN_BASE_URL + href

                urls.append(href)
                logger.debug(f"  - {company_name}: {href}")

        # 중복 제거
        return list(set(urls))
