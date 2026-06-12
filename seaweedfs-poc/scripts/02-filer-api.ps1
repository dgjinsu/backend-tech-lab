# =============================================================================
# Phase 1-B : Filer HTTP API 실습 (경로 기반 파일시스템 추상화)
# -----------------------------------------------------------------------------
# Phase 1-A의 네이티브 API는 fid("7,01caf19d6f") 라는 기계적인 ID로 파일을 다뤘다.
# 실무에서 이걸 직접 관리하긴 불편하다. 그래서 filer가 있다.
#
#   filer = volume 위에 "디렉터리/파일 경로" 레이어를 얹은 것.
#           /docs/report.txt 같은 사람이 읽는 경로로 접근.
#           내부적으로 filer가 알아서 assign+upload 하고, 경로->fid 매핑을
#           자신의 메타데이터 저장소에 기록한다.
#
# 즉, 우리가 평소 쓰는 "파일 서버"처럼 동작한다. S3 게이트웨이도 이 filer 위에 얹힌다.
#
# 실행:  powershell -ExecutionPolicy Bypass -File .\scripts\02-filer-api.ps1
# =============================================================================
$ErrorActionPreference = 'Stop'
$FILER = "http://localhost:8888"
$root  = Split-Path $PSScriptRoot -Parent
$downloads = Join-Path $root "downloads"
New-Item -ItemType Directory -Force -Path $downloads | Out-Null

function Section($t) { Write-Host "`n========== $t ==========" -ForegroundColor Cyan }

# -----------------------------------------------------------------------------
Section "1) UPLOAD — 경로를 지정해서 바로 업로드 (assign 불필요)"
# 네이티브 API와 달리 master에게 assign을 따로 안 한다. filer가 다 처리한다.
$sample = Join-Path $root "_sample.txt"
"Filer로 올린 파일입니다. $(Get-Date -Format o)" | Out-File $sample -Encoding utf8
# /docs/report.txt 라는 경로에 저장
curl.exe -s -F "file=@$sample" "$FILER/docs/report.txt" | Out-Null
Write-Host "  -> $FILER/docs/report.txt 에 저장됨"

# -----------------------------------------------------------------------------
Section "2) LIST — 디렉터리 내용 조회 (JSON)"
# Accept: application/json 헤더를 주면 디렉터리 목록을 JSON으로 받는다.
$list = Invoke-RestMethod "$FILER/docs/?limit=100" -Headers @{ Accept = "application/json" }
$list.Entries | Select-Object FullPath, @{N='size';E={$_.FileSize}}, Mime | Format-Table -AutoSize

# -----------------------------------------------------------------------------
Section "3) DOWNLOAD — 경로로 바로 읽기"
$out = Join-Path $downloads "from-filer.txt"
curl.exe -s "$FILER/docs/report.txt" -o $out
Write-Host "  -> 내용: $(Get-Content $out -Raw)"

# -----------------------------------------------------------------------------
Section "4) METADATA — 파일의 fid/청크 정보 들여다보기"
# filer가 이 경로를 어떤 volume의 어떤 fid로 매핑했는지 볼 수 있다.
$meta = Invoke-RestMethod "$FILER/docs/report.txt?metadata=true" -Headers @{ Accept = "application/json" }
$meta | ConvertTo-Json -Depth 6

# -----------------------------------------------------------------------------
Section "5) DELETE — 파일/디렉터리 삭제"
curl.exe -s -X DELETE "$FILER/docs/report.txt" | Out-Null
# 디렉터리째 지우려면: curl -X DELETE "$FILER/docs/?recursive=true"
curl.exe -s -X DELETE "$FILER/docs/?recursive=true" | Out-Null
Write-Host "  -> /docs/report.txt 및 /docs 디렉터리 삭제 완료"

Remove-Item $sample -ErrorAction SilentlyContinue
Write-Host "`n[완료] Filer API 한 바퀴 끝. downloads\from-filer.txt 확인해보세요." -ForegroundColor Green
