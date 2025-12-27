# 本地构建脚本 (PowerShell 版本)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Green
Write-Host "本地构建脚本" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# 检查 Node.js
$nodeVersion = Get-Command node -ErrorAction SilentlyContinue
if (-not $nodeVersion) {
    Write-Host "错误: 未安装 Node.js" -ForegroundColor Red
    Write-Host "请先安装 Node.js: https://nodejs.org/" -ForegroundColor Yellow
    exit 1
}

Write-Host "Node.js 版本: $(node --version)" -ForegroundColor Cyan
Write-Host "npm 版本: $(npm --version)" -ForegroundColor Cyan
Write-Host ""

# 1. 构建前端
Write-Host "[1/2] 构建前端项目..." -ForegroundColor Yellow
Set-Location frontend

if (-not (Test-Path "node_modules")) {
    Write-Host "安装前端依赖..." -ForegroundColor Cyan
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "前端依赖安装失败！" -ForegroundColor Red
        exit 1
    }
}

Write-Host "开始构建前端..." -ForegroundColor Cyan
npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Host "前端构建失败！" -ForegroundColor Red
    Set-Location ..
    exit 1
}

if (-not (Test-Path "dist")) {
    Write-Host "前端构建失败！未找到 dist 目录" -ForegroundColor Red
    Set-Location ..
    exit 1
}

Write-Host "✓ 前端构建完成" -ForegroundColor Green
Write-Host ""

# 2. 构建管理后台
Set-Location ..
Write-Host "[2/2] 构建管理后台..." -ForegroundColor Yellow
Set-Location admin

if (-not (Test-Path "node_modules")) {
    Write-Host "安装管理后台依赖..." -ForegroundColor Cyan
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "管理后台依赖安装失败！" -ForegroundColor Red
        Set-Location ..
        exit 1
    }
}

Write-Host "开始构建管理后台..." -ForegroundColor Cyan
npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Host "管理后台构建失败！" -ForegroundColor Red
    Set-Location ..
    exit 1
}

if (-not (Test-Path "dist")) {
    Write-Host "管理后台构建失败！未找到 dist 目录" -ForegroundColor Red
    Set-Location ..
    exit 1
}

Write-Host "✓ 管理后台构建完成" -ForegroundColor Green
Write-Host ""

# 3. 创建构建产物压缩包
Set-Location ..
Write-Host "创建构建产物压缩包..." -ForegroundColor Yellow

$buildDir = "build-artifacts"
if (Test-Path $buildDir) {
    Remove-Item -Recurse -Force $buildDir
}
New-Item -ItemType Directory -Path $buildDir | Out-Null

# 复制前端构建产物
Write-Host "打包前端构建产物..." -ForegroundColor Cyan
Copy-Item -Recurse frontend/dist "$buildDir/frontend-dist"

# 复制管理后台构建产物
Write-Host "打包管理后台构建产物..." -ForegroundColor Cyan
Copy-Item -Recurse admin/dist "$buildDir/admin-dist"

# 创建压缩包
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$archiveName = "build-artifacts-$timestamp.zip"

Compress-Archive -Path "$buildDir/*" -DestinationPath $archiveName -Force

Write-Host "构建产物已打包: $archiveName" -ForegroundColor Green
Write-Host ""

# 清理临时目录
Remove-Item -Recurse -Force $buildDir

Write-Host "========================================" -ForegroundColor Green
Write-Host "构建完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "构建产物: $archiveName" -ForegroundColor Cyan
Write-Host ""
Write-Host "上传到服务器后，执行以下命令解压和部署：" -ForegroundColor Yellow
Write-Host "  unzip $archiveName" -ForegroundColor Cyan
Write-Host "  cp -r frontend-dist/* /home/bjzxjj/frontend/" -ForegroundColor Cyan
Write-Host "  cp -r admin-dist/* /home/bjzxjj/admin/" -ForegroundColor Cyan
Write-Host ""

