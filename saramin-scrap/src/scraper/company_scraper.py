from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from bs4 import BeautifulSoup
from src.models.company import Company
import logging
import re
import time

logger = logging.getLogger(__name__)

class CompanyScraper:
    """회사 상세 페이지 스크래퍼

    크롤링 흐름:
    1. view-inner-recruit 페이지 로드 (회사 이름 추출)
    2. 기업소개 버튼에서 view?csn= URL 추출
    3. view?csn= 페이지에서 회사 정보 추출
    """

    def __init__(self, driver):
        self.driver = driver

    def scrape_company(self, inner_recruit_url: str) -> Company:
        """회사 정보 추출 (2단계 크롤링)

        Args:
            inner_recruit_url: view-inner-recruit URL

        Returns:
            Company 객체
        """
        logger.info(f"🏢 회사 정보 크롤링 시작: {inner_recruit_url}")

        try:
            # 1단계: view-inner-recruit 페이지에서 회사명과 기업소개 URL 추출
            self.driver.get(inner_recruit_url)
            time.sleep(2)

            soup = BeautifulSoup(self.driver.page_source, 'lxml')

            # 회사 이름 추출
            company_name = self._extract_company_name(soup)
            logger.info(f"  📌 회사명: {company_name}")

            # 기업소개 버튼에서 view?csn= URL 추출
            company_info_url = self._extract_company_info_url(soup)

            if not company_info_url:
                logger.warning(f"  ⚠️ 기업소개 URL을 찾을 수 없습니다")
                return Company(name=company_name, source_url=inner_recruit_url)

            logger.info(f"  🔗 기업소개 URL: {company_info_url}")

            # 2단계: 기업소개 페이지에서 상세 정보 추출
            self.driver.get(company_info_url)
            time.sleep(2)

            soup = BeautifulSoup(self.driver.page_source, 'lxml')

            # 사원수, 위치, 매출액 추출
            employee_count = self._extract_employee_count(soup)
            location = self._extract_location(soup)
            revenue = self._extract_revenue(soup)

            # 3단계: 연봉정보 페이지에서 평균연봉 추출
            salary_url = self._extract_salary_info_url(soup)
            average_salary = '-'

            if salary_url:
                logger.info(f"  💰 연봉정보 URL: {salary_url}")
                self.driver.get(salary_url)
                time.sleep(2)

                salary_soup = BeautifulSoup(self.driver.page_source, 'lxml')
                average_salary = self._extract_average_salary(salary_soup)
            else:
                logger.warning(f"  ⚠️ 연봉정보 URL을 찾을 수 없습니다")

            # 데이터 통합
            company = Company(
                name=company_name,
                employee_count=employee_count,
                location=location,
                salary=average_salary,  # 평균연봉
                source_url=company_info_url
            )

            logger.info(f"✅ '{company.name}' 정보 추출 완료")
            logger.info(f"   - 사원수: {company.employee_count}")
            logger.info(f"   - 위치: {company.location}")
            logger.info(f"   - 평균연봉: {company.salary}")

            return company

        except Exception as e:
            logger.error(f"❌ 회사 정보 추출 실패: {e}", exc_info=True)
            return Company(name="오류", source_url=inner_recruit_url)

    def _extract_company_info_url(self, soup: BeautifulSoup) -> str:
        """기업소개 버튼에서 회사 상세 페이지 URL 추출"""
        # 기업소개 버튼 찾기
        button = soup.find('button', class_='btn_menu', onclick=lambda x: x and 'company-info/view?csn=' in x)

        if button and button.get('onclick'):
            onclick = button['onclick']
            # onclick="window.location.href='/zf_user/company-info/view?csn=...'" 에서 URL 추출
            match = re.search(r"window\.location\.href='([^']+)'", onclick)
            if match:
                url = match.group(1)
                # 상대 URL을 절대 URL로 변환
                if url.startswith('/'):
                    from src.config import SARAMIN_BASE_URL
                    url = SARAMIN_BASE_URL + url
                return url

        return None

    def _extract_salary_info_url(self, soup: BeautifulSoup) -> str:
        """연봉정보 버튼에서 URL 추출"""
        # 연봉정보 버튼 찾기
        button = soup.find('button', class_='btn_menu', onclick=lambda x: x and 'company-info/view-inner-salary?csn=' in x)

        if button and button.get('onclick'):
            onclick = button['onclick']
            # onclick="window.location.href='/zf_user/company-info/view-inner-salary?csn=...'" 에서 URL 추출
            match = re.search(r"window\.location\.href='([^']+)'", onclick)
            if match:
                url = match.group(1)
                # 상대 URL을 절대 URL로 변환
                if url.startswith('/'):
                    from src.config import SARAMIN_BASE_URL
                    url = SARAMIN_BASE_URL + url
                return url

        return None

    def _extract_average_salary(self, soup: BeautifulSoup) -> str:
        """평균연봉 추출 (연봉정보 페이지에서)"""
        # p.average_currency에서 평균연봉 추출
        currency_elem = soup.select_one('p.average_currency em')
        if currency_elem:
            amount = currency_elem.get_text(strip=True)
            # "4,995" 같은 형태 → "4,995만원"
            return f"{amount}만원"

        return '-'

    def _extract_company_name(self, soup: BeautifulSoup) -> str:
        """회사 이름 추출 (view-inner-recruit 페이지에서)"""
        # a.str_tit 링크에서 회사명 추출
        link = soup.select_one('a.str_tit')
        if link:
            name = link.get_text(strip=True)
            if name:
                return name

        # title에서 추출
        title = soup.find('title')
        if title:
            match = re.search(r'^(.+?)\s*[-|]', title.get_text())
            if match:
                return match.group(1).strip()

        return "이름 없음"

    def _extract_employee_count(self, soup: BeautifulSoup) -> str:
        """사원수 추출 (company_summary에서)"""
        # li.company_summary_item 중 "사원수" 텍스트 포함된 것 찾기
        summary_items = soup.select('li.company_summary_item')

        for item in summary_items:
            desc = item.select_one('.company_summary_desc')
            if desc and '사원수' in desc.get_text():
                # company_summary_tit에서 숫자 추출
                tit = item.select_one('.company_summary_tit')
                if tit:
                    text = tit.get_text(strip=True)
                    # "23명" 같은 형태
                    return text

        return '-'

    def _extract_location(self, soup: BeautifulSoup) -> str:
        """회사 위치 추출"""
        # company_summary에서 주소 찾기
        summary_items = soup.select('li.company_summary_item')

        for item in summary_items:
            desc = item.select_one('.company_summary_desc')
            if desc and any(keyword in desc.get_text() for keyword in ['주소', '위치', '소재지']):
                tit = item.select_one('.company_summary_tit')
                if tit:
                    return tit.get_text(strip=True)

        # 텍스트에서 주소 패턴 찾기
        pattern = r'(?:주소|위치|소재지)\s*[:：]?\s*([^\n]+(?:시|구|동)[^\n]*)'
        match = re.search(pattern, soup.get_text())
        if match:
            return match.group(1).strip()

        return '-'

    def _extract_revenue(self, soup: BeautifulSoup) -> str:
        """매출액 추출 (기업소개 페이지에서)"""
        # company_summary에서 매출액 찾기
        summary_items = soup.select('li.company_summary_item')

        for item in summary_items:
            desc = item.select_one('.company_summary_desc')
            if desc and '매출액' in desc.get_text():
                tit = item.select_one('.company_summary_tit')
                if tit:
                    # "19억 331만원" 같은 형태
                    return tit.get_text(strip=True)

        return '-'
