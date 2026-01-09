from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
from fake_useragent import UserAgent
import logging

logger = logging.getLogger(__name__)

class BrowserManager:
    """Selenium 브라우저 관리 클래스"""

    def __init__(self, headless: bool = False):
        self.headless = headless
        self.driver = None

    def __enter__(self):
        """Context manager 진입 - 브라우저 초기화"""
        self.driver = self._create_driver()
        return self.driver

    def __exit__(self, exc_type, exc_val, exc_tb):
        """Context manager 종료 - 브라우저 정리"""
        if self.driver:
            self.driver.quit()
            logger.info("브라우저 종료")

    def _create_driver(self) -> webdriver.Chrome:
        """Chrome WebDriver 생성 및 설정"""
        options = Options()

        # Headless 모드 설정
        if self.headless:
            options.add_argument('--headless')

        # 기본 옵션
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument('--disable-blink-features=AutomationControlled')

        # PC 버전으로 열기 위한 설정
        options.add_argument('--window-size=1920,1080')
        options.add_argument('--start-maximized')

        # PC 버전 User-Agent 설정 (모바일 방지)
        pc_user_agent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
        options.add_argument(f'user-agent={pc_user_agent}')

        # 자동화 플래그 숨기기
        options.add_experimental_option("excludeSwitches", ["enable-automation"])
        options.add_experimental_option('useAutomationExtension', False)

        # WebDriver 생성
        service = Service(ChromeDriverManager().install())
        driver = webdriver.Chrome(service=service, options=options)

        # navigator.webdriver 플래그 제거
        driver.execute_script(
            "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
        )

        logger.info("Chrome 브라우저 초기화 완료 (PC 버전)")
        return driver
