# object-storage 로컬 스택 기동 + Garage 부트스트랩
#
# 두 오브젝트 스토리지(SeaweedFS, Garage)를 띄우고 Garage 초기화까지 한 번에 수행한다.
#   powershell -ExecutionPolicy Bypass -File .\up.ps1

$ErrorActionPreference = 'Continue'
$here    = Split-Path -Parent $MyInvocation.MyCommand.Path
$compose = Join-Path $here '..\docker\docker-compose.yml'
$init    = Join-Path $here '..\docker\garage-init.ps1'

Write-Host '[up] docker compose up -d'
docker compose -f $compose up -d
if ($LASTEXITCODE -ne 0) { Write-Error '[up] docker compose 기동 실패'; exit 1 }

Write-Host ''
Write-Host '[up] Garage 부트스트랩'
& $init

Write-Host ''
Write-Host '[up] 완료. 이제 앱을 실행하세요:  ./gradlew bootRun'
Write-Host '  - SeaweedFS S3 : http://localhost:8333   (Master UI http://localhost:9333)'
Write-Host '  - Garage S3    : http://localhost:3900   (Admin    http://localhost:3903)'
Write-Host '  - 앱 UI        : http://localhost:8092'
