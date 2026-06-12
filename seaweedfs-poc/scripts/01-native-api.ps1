# =============================================================================
# Phase 1-A : SeaweedFS 네이티브 HTTP API 실습 (Haystack 모델 체감)
# -----------------------------------------------------------------------------
# 이 스크립트는 SeaweedFS의 가장 밑바닥 흐름을 보여준다.
#
#   assign(master) -> upload(volume) -> download(volume) -> lookup(master) -> delete(volume)
#
# 핵심 개념
#   * 파일을 저장하려면 먼저 master에게 "어디에 쓸까요?"를 물어본다(assign).
#     master는 fid(파일 ID)와 그 fid를 저장할 volume의 주소를 돌려준다.
#   * 실제 업로드/다운로드는 master가 아니라 volume 서버에 직접 한다.
#     => master는 트래픽 병목이 되지 않는다. (메타데이터만 다룸)
#   * fid = "7,01caf19d6f"  ->  앞 숫자 7 = volume id, 뒤 = 그 안에서의 file key.
#
# 실행:  powershell -ExecutionPolicy Bypass -File .\scripts\01-native-api.ps1
# =============================================================================
$ErrorActionPreference = 'Stop'
$MASTER = "http://localhost:9333"
$root   = Split-Path $PSScriptRoot -Parent
$downloads = Join-Path $root "downloads"
New-Item -ItemType Directory -Force -Path $downloads | Out-Null

function Section($t) { Write-Host "`n========== $t ==========" -ForegroundColor Cyan }

# -----------------------------------------------------------------------------
Section "1) ASSIGN — master에게 저장 위치 할당 요청"
# master는 실제 데이터를 받지 않는다. "이 fid로, 이 volume에 써라"만 알려준다.
$assign = Invoke-RestMethod "$MASTER/dir/assign"
$assign | ConvertTo-Json
$fid      = $assign.fid
$volumeId = $fid.Split(',')[0]
# [중요] 호스트(도커 밖)에서는 url(volume:8080)이 아니라 publicUrl(localhost:8085)로 접근.
$volUrl   = "http://$($assign.publicUrl)"
Write-Host "  -> fid       = $fid   (volume id = $volumeId)"
Write-Host "  -> 업로드 대상 = $volUrl/$fid"

# -----------------------------------------------------------------------------
Section "2) UPLOAD — volume 서버에 파일 직접 업로드"
# multipart/form-data 로 올린다. (curl.exe는 Windows 10+ 기본 내장)
$sample = Join-Path $root "_sample.txt"
"Hello SeaweedFS! 시각: $(Get-Date -Format o)" | Out-File $sample -Encoding utf8
$upload = curl.exe -s -F "file=@$sample" "$volUrl/$fid" | ConvertFrom-Json
$upload | ConvertTo-Json
Write-Host "  -> 저장된 크기 = $($upload.size) bytes, eTag = $($upload.eTag)"

# -----------------------------------------------------------------------------
Section "3) DOWNLOAD — fid 로 다시 읽기"
$out = Join-Path $downloads "from-native.txt"
curl.exe -s "$volUrl/$fid" -o $out
Write-Host "  -> 다운로드 내용: $(Get-Content $out -Raw)"

# -----------------------------------------------------------------------------
Section "4) LOOKUP — fid 의 volume 위치를 master 에게 조회"
# 클라이언트는 fid만 들고 있어도, master에게 물어 volume 위치를 알아낼 수 있다.
Invoke-RestMethod "$MASTER/dir/lookup?volumeId=$volumeId" | ConvertTo-Json -Depth 5

# -----------------------------------------------------------------------------
Section "5) DELETE — fid 삭제 후 재조회로 확인"
curl.exe -s -X DELETE "$volUrl/$fid" | Out-Null
$code = curl.exe -s -o NUL -w "%{http_code}" "$volUrl/$fid"
Write-Host "  -> 삭제 후 GET 응답코드 = $code  (404면 삭제 성공)"

Remove-Item $sample -ErrorAction SilentlyContinue
Write-Host "`n[완료] 네이티브 API 한 바퀴 끝. downloads\from-native.txt 확인해보세요." -ForegroundColor Green
