from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Paragraph, Spacer
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from typing import List
from pathlib import Path
import logging
from datetime import datetime

logger = logging.getLogger(__name__)

def export_to_pdf(companies: List[dict], output_path: str):
    """회사 데이터를 PDF 파일로 저장

    Args:
        companies: Company 객체의 딕셔너리 리스트
        output_path: 저장할 PDF 파일 경로
    """
    if not companies:
        logger.warning("저장할 데이터가 없습니다")
        return

    # 출력 경로 생성
    output_path = Path(output_path)
    output_path.parent.mkdir(parents=True, exist_ok=True)

    # PDF 문서 생성
    doc = SimpleDocTemplate(
        str(output_path),
        pagesize=A4,
        rightMargin=30,
        leftMargin=30,
        topMargin=30,
        bottomMargin=18,
    )

    # 스토리 (PDF 내용)
    story = []

    # 제목
    title_style = ParagraphStyle(
        'CustomTitle',
        parent=getSampleStyleSheet()['Heading1'],
        fontSize=20,
        textColor=colors.HexColor('#1a1a1a'),
        spaceAfter=30,
        alignment=1  # 중앙 정렬
    )

    title = Paragraph("사람인 크롤링 결과", title_style)
    story.append(title)

    # 생성 일시
    info_style = ParagraphStyle(
        'InfoStyle',
        parent=getSampleStyleSheet()['Normal'],
        fontSize=10,
        textColor=colors.HexColor('#666666'),
        spaceAfter=20,
        alignment=1
    )

    timestamp = datetime.now().strftime('%Y년 %m월 %d일 %H시 %M분')
    info_text = Paragraph(f"생성일시: {timestamp} | 총 {len(companies)}개 회사", info_style)
    story.append(info_text)
    story.append(Spacer(1, 20))

    # 테이블 데이터 준비
    table_data = [['번호', '회사명', '사원수', '위치', '평균연봉', '크롤링시각']]

    for idx, company in enumerate(companies, 1):
        row = [
            str(idx),
            company.get('name', '-'),
            company.get('employee_count', '-'),
            company.get('location', '-')[:30] + '...' if len(company.get('location', '-')) > 30 else company.get('location', '-'),
            company.get('salary', '-'),
            company.get('scraped_at', '-')
        ]
        table_data.append(row)

    # 테이블 생성
    col_widths = [30, 80, 60, 150, 80, 100]  # 각 컬럼 너비
    table = Table(table_data, colWidths=col_widths)

    # 테이블 스타일
    table.setStyle(TableStyle([
        # 헤더 스타일
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#4472C4')),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, 0), 'CENTER'),
        ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
        ('FONTSIZE', (0, 0), (-1, 0), 10),
        ('BOTTOMPADDING', (0, 0), (-1, 0), 12),

        # 데이터 행 스타일
        ('BACKGROUND', (0, 1), (-1, -1), colors.white),
        ('TEXTCOLOR', (0, 1), (-1, -1), colors.black),
        ('ALIGN', (0, 1), (0, -1), 'CENTER'),  # 번호 컬럼 중앙 정렬
        ('ALIGN', (2, 1), (2, -1), 'CENTER'),  # 사원수 컬럼 중앙 정렬
        ('ALIGN', (4, 1), (4, -1), 'CENTER'),  # 평균연봉 컬럼 중앙 정렬
        ('FONTNAME', (0, 1), (-1, -1), 'Helvetica'),
        ('FONTSIZE', (0, 1), (-1, -1), 8),
        ('TOPPADDING', (0, 1), (-1, -1), 6),
        ('BOTTOMPADDING', (0, 1), (-1, -1), 6),

        # 테두리
        ('GRID', (0, 0), (-1, -1), 0.5, colors.grey),

        # 교대 행 배경색
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor('#F2F2F2')]),
    ]))

    story.append(table)

    # PDF 빌드
    doc.build(story)
    logger.info(f"✅ PDF 저장 완료: {output_path} ({len(companies)}개 회사)")
