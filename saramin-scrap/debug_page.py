"""페이지 소스 분석용 디버그 스크립트"""
from src.utils.browser import BrowserManager
import time

url = "https://www.saramin.co.kr/zf_user/jobs/list/job-category?page=1&cat_kewd=291%2C238%2C235%2C292&exp_cd=1&search_optional_item=y&search_done=y&panel_count=y&preview=y&isAjaxRequest=0&page_count=50&sort=RL&type=job-category&is_param=1&isSearchResultEmpty=1&isSectionHome=0&searchParamCount=2&tab=job-category"

print("🔍 페이지 소스 분석 시작...")

with BrowserManager(headless=False) as driver:
    driver.get(url)
    print("⏳ 페이지 로딩 대기 중... (5초)")
    time.sleep(5)

    # HTML 저장
    html = driver.page_source

    with open('debug_page_source.html', 'w', encoding='utf-8') as f:
        f.write(html)

    print(f"✅ 페이지 소스 저장 완료: debug_page_source.html")
    print(f"📊 HTML 크기: {len(html):,} bytes")

    # 회사 관련 링크 찾기
    from bs4 import BeautifulSoup
    soup = BeautifulSoup(html, 'lxml')

    # 모든 링크 중 company-info 포함된 것 찾기
    all_links = soup.find_all('a', href=True)
    company_links = [a for a in all_links if 'company-info' in a.get('href', '')]

    print(f"\n📋 'company-info' 포함 링크: {len(company_links)}개")

    if company_links:
        print("\n🔗 첫 5개 회사 링크:")
        for idx, link in enumerate(company_links[:5], 1):
            href = link.get('href')
            text = link.get_text(strip=True)
            parent_class = link.parent.get('class', []) if link.parent else []
            print(f"  {idx}. 텍스트: {text}")
            print(f"     URL: {href}")
            print(f"     부모 클래스: {parent_class}")
            print()

    # 공고 아이템 찾기
    print("\n🔍 공고 아이템 구조 분석:")

    selectors_to_try = [
        ('div.item_recruit', '일반적인 채용 아이템'),
        ('div[class*="recruit"]', 'recruit 포함 클래스'),
        ('div.recruit_info', '채용 정보'),
        ('div.list_item', '리스트 아이템'),
        ('.item_recruit', '아이템 채용'),
    ]

    for selector, desc in selectors_to_try:
        items = soup.select(selector)
        if items:
            print(f"✅ '{selector}' ({desc}): {len(items)}개 발견")

            # 첫 번째 아이템의 구조 출력
            if items:
                first_item = items[0]
                print(f"   첫 번째 아이템 클래스: {first_item.get('class', [])}")

                # 회사명 찾기
                company_names = first_item.find_all('a', class_=lambda x: x and 'corp' in str(x).lower())
                if company_names:
                    print(f"   회사명 링크 발견: {len(company_names)}개")
                    for cn in company_names[:2]:
                        print(f"     - 클래스: {cn.get('class', [])}")
                        print(f"     - 텍스트: {cn.get_text(strip=True)}")
        else:
            print(f"❌ '{selector}' ({desc}): 발견 안됨")

    print("\n✅ 분석 완료! debug_page_source.html 파일을 확인하세요.")
