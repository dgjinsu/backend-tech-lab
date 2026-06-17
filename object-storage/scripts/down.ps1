# object-storage 로컬 스택 정지
#
#   ./down.ps1         정지(데이터 유지)
#   ./down.ps1 -Reset  정지 + 볼륨 삭제(데이터 완전 초기화)

param([switch]$Reset)

$ErrorActionPreference = 'Continue'
$here    = Split-Path -Parent $MyInvocation.MyCommand.Path
$compose = Join-Path $here '..\docker\docker-compose.yml'

if ($Reset) {
    Write-Host '[down] docker compose down -v (볼륨/데이터 삭제)'
    docker compose -f $compose down -v
} else {
    Write-Host '[down] docker compose down (데이터 유지)'
    docker compose -f $compose down
}
