import os
from dotenv import load_dotenv

# .env 파일 로드
load_dotenv()

# 브라우저 설정
HEADLESS = os.getenv('HEADLESS', 'false').lower() == 'true'
WINDOW_WIDTH = int(os.getenv('WINDOW_WIDTH', '1920'))
WINDOW_HEIGHT = int(os.getenv('WINDOW_HEIGHT', '1080'))

# 크롤링 속도 설정
MIN_DELAY = float(os.getenv('MIN_DELAY', '2.0'))
MAX_DELAY = float(os.getenv('MAX_DELAY', '3.0'))
REQUEST_TIMEOUT = int(os.getenv('REQUEST_TIMEOUT', '30'))

# 재시도 설정
MAX_RETRIES = int(os.getenv('MAX_RETRIES', '3'))

# 출력 설정
OUTPUT_DIR = os.getenv('OUTPUT_DIR', './data')
LOG_LEVEL = os.getenv('LOG_LEVEL', 'INFO')

# 사람인 기본 URL
SARAMIN_BASE_URL = 'https://www.saramin.co.kr'
