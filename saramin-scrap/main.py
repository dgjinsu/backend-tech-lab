import logging
import time
import random
from datetime import datetime
from pathlib import Path

from src.utils.browser import BrowserManager
from src.scraper.listing_scraper import ListingScraper
from src.scraper.company_scraper import CompanyScraper
from src.utils.pdf_export import export_to_pdf
from src.config import MIN_DELAY, MAX_DELAY, OUTPUT_DIR, LOG_LEVEL

# 로깅 설정
logging.basicConfig(
    level=getattr(logging, LOG_LEVEL),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler('logs/saramin_scraper.log', encoding='utf-8')
    ]
)
logger = logging.getLogger(__name__)


def main():
    """메인 실행 함수"""
    print("=" * 80)
    print("🚀 사람인 채용 공고 크롤러")
    print("=" * 80)
    print()

    # 사용자 입력 받기
    print("📋 크롤링할 채용 공고 리스트 URL을 입력하세요.")
    print("   예시: https://www.saramin.co.kr/zf_user/jobs/list/job-category?page=1&cat_kewd=291,238,235,292")
    url = input("URL: ").strip()
    print()

    print("📄 크롤링할 페이지 범위를 입력하세요.")
    while True:
        try:
            start_page = int(input("   시작 페이지 (예: 1): ").strip())
            if start_page < 1:
                print("   ⚠️ 1 이상의 숫자를 입력해주세요.")
                continue
            break
        except ValueError:
            print("   ⚠️ 숫자를 입력해주세요.")

    while True:
        try:
            end_page = int(input("   끝 페이지 (예: 5): ").strip())
            if end_page < start_page:
                print(f"   ⚠️ 시작 페이지({start_page})보다 큰 숫자를 입력해주세요.")
                continue
            break
        except ValueError:
            print("   ⚠️ 숫자를 입력해주세요.")
    print()

    print("📁 결과 파일명을 입력하세요 (Enter 키를 누르면 자동 생성).")
    output_name = input("   파일명 (예: my_companies.pdf): ").strip()
    print()

    # 출력 파일명 생성
    if output_name:
        # 확장자가 없으면 .pdf 추가
        if not output_name.endswith('.pdf'):
            output_name += '.pdf'
        output_path = Path(OUTPUT_DIR) / output_name
    else:
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        output_path = Path(OUTPUT_DIR) / f'saramin_{timestamp}.pdf'

    logger.info("=" * 80)
    logger.info("🚀 사람인 크롤러 시작")
    logger.info(f"📌 URL: {url}")
    logger.info(f"📌 페이지 범위: {start_page} ~ {end_page}")
    logger.info(f"📌 출력 파일: {output_path}")
    logger.info("=" * 80)

    companies = []
    total_companies = 0

    try:
        # 브라우저 시작
        with BrowserManager(headless=False) as driver:
            listing_scraper = ListingScraper(driver)
            company_scraper = CompanyScraper(driver)

            # 각 페이지 크롤링
            for page in range(start_page, end_page + 1):
                logger.info(f"\n{'='*60}")
                logger.info(f"📄 [{page}/{end_page}] 페이지 처리 중...")
                logger.info(f"{'='*60}")

                try:
                    # 1. 공고 리스트에서 회사 URL 추출
                    company_urls = listing_scraper.scrape_listing_page(url, page)

                    if not company_urls:
                        logger.warning(f"⚠️ 페이지 {page}에서 회사 URL을 찾지 못했습니다")
                        continue

                    # 2. 각 회사 상세 페이지 크롤링
                    for idx, url in enumerate(company_urls, 1):
                        logger.info(f"  [{idx}/{len(company_urls)}] 크롤링 중...")

                        try:
                            company = company_scraper.scrape_company(url)
                            companies.append(company.to_dict())
                            total_companies += 1

                            # 크롤링 속도 제어 (2-3초 대기)
                            delay = random.uniform(MIN_DELAY, MAX_DELAY)
                            time.sleep(delay)

                        except Exception as e:
                            logger.error(f"  ❌ 회사 크롤링 실패 ({url}): {e}")
                            continue

                    # 페이지 간 대기
                    time.sleep(random.uniform(MIN_DELAY, MAX_DELAY))

                except Exception as e:
                    logger.error(f"❌ 페이지 {page} 처리 실패: {e}")
                    continue

            # PDF 저장
            logger.info("\n" + "=" * 80)
            logger.info("💾 데이터 저장 중...")
            export_to_pdf(companies, str(output_path))

            # 완료 메시지
            logger.info("=" * 80)
            logger.info("✅ 크롤링 완료!")
            logger.info(f"📊 총 {total_companies}개 회사 정보 수집")
            logger.info(f"📁 파일 저장: {output_path}")
            logger.info("=" * 80)

    except KeyboardInterrupt:
        logger.warning("\n⚠️ 사용자에 의해 중단되었습니다")
        if companies:
            logger.info("💾 수집된 데이터 저장 중...")
            export_to_pdf(companies, str(output_path))
            logger.info(f"📁 파일 저장: {output_path}")

    except Exception as e:
        logger.error(f"❌ 오류 발생: {e}", exc_info=True)
        if companies:
            logger.info("💾 수집된 데이터 저장 중...")
            export_to_pdf(companies, str(output_path))


if __name__ == '__main__':
    main()
