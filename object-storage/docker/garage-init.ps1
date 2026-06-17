# Garage 단일 노드 부트스트랩 (멱등 실행)
#
# docker compose 로 garage 컨테이너가 떠 있는 상태에서 1회 실행한다.
#   layout 배정 -> 적용 -> 버킷 생성 -> 결정적 키 import -> 권한 부여
# 이미 초기화돼 있으면 아무 작업도 하지 않고 종료한다.
#
#   powershell -ExecutionPolicy Bypass -File .\garage-init.ps1

$ErrorActionPreference = 'Continue'   # 네이티브 stderr(INFO 로그) 때문에 throw 되지 않도록

$Container = 'os-garage'
$Zone      = 'dc1'
$Capacity  = '1GB'
$Bucket    = 'poc-bucket'
$KeyName   = 'poc-app-key'
# 앱(application.yml)의 storage.backends.garage 자격증명과 반드시 일치해야 한다.
$AccessKey = 'GK0123456789abcdef01234567'
$SecretKey = '0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef'

function Garage { docker exec $Container /garage @args }

# 1) Garage 노드가 응답할 때까지 대기 (최대 30초)
Write-Host '[garage-init] 노드 응답 대기...'
$ready = $false
for ($i = 0; $i -lt 30; $i++) {
    $status = (Garage status 2>&1 | Out-String)
    if ($status -match 'HEALTHY NODES') { $ready = $true; break }
    Start-Sleep -Seconds 1
}
if (-not $ready) { Write-Error 'Garage 노드가 30초 내에 준비되지 않았습니다.'; exit 1 }

# 2) 이미 초기화됐는지 확인 (키 + 버킷 모두 존재하면 건너뜀)
$keyList    = (Garage key list 2>&1 | Out-String)
$bucketList = (Garage bucket list 2>&1 | Out-String)
if (($keyList -match [regex]::Escape($AccessKey)) -and ($bucketList -match [regex]::Escape($Bucket))) {
    Write-Host "[garage-init] 이미 초기화됨 (key=$AccessKey, bucket=$Bucket). 건너뜁니다."
    Garage bucket info $Bucket
    exit 0
}

# 3) 노드 ID 획득 후 layout 배정 ('<hex>@<addr>' 에서 hex 부분만 사용)
$nodeId = (((Garage node id -q) -split '@')[0]).Trim()
Write-Host "[garage-init] node=$nodeId 에 layout 배정 (zone=$Zone, capacity=$Capacity)"
Garage layout assign -z $Zone -c $Capacity $nodeId | Out-Null

# 4) staged 버전 파싱 후 적용
$show = (Garage layout show 2>&1 | Out-String)
if ($show -match '--version\s+(\d+)') {
    $ver = $Matches[1]
    Write-Host "[garage-init] layout apply --version $ver"
    Garage layout apply --version $ver | Out-Null
} else {
    Write-Host '[garage-init] 적용할 staged 변경이 없습니다.'
}

# 5) 버킷 생성 (이미 있으면 무시)
Garage bucket create $Bucket | Out-Null
if ($LASTEXITCODE -eq 0) { Write-Host "[garage-init] 버킷 생성: $Bucket" }
else { Write-Host "[garage-init] 버킷 생성 건너뜀(이미 존재 가능): $Bucket" }

# 6) 결정적 키 import (이미 있으면 무시)
Garage key import --yes -n $KeyName $AccessKey $SecretKey | Out-Null
if ($LASTEXITCODE -eq 0) { Write-Host "[garage-init] 키 import: $AccessKey" }
else { Write-Host "[garage-init] 키 import 건너뜀(이미 존재 가능): $AccessKey" }

# 7) 버킷 권한 부여 (RWO, 멱등)
Garage bucket allow --read --write --owner $Bucket --key $KeyName | Out-Null
Write-Host '[garage-init] 권한 부여 완료 (RWO)'

Write-Host ''
Write-Host '[garage-init] 완료. 최종 상태:'
Garage bucket info $Bucket
