# Node.js 环境检查脚本

Write-Host "========================================" -ForegroundColor Green
Write-Host "Node.js 环境检查" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# 检查 Node.js
Write-Host "检查 Node.js..." -ForegroundColor Yellow
$nodeVersion = Get-Command node -ErrorAction SilentlyContinue
if ($nodeVersion) {
    $version = node --version
    Write-Host "✓ Node.js 已安装: $version" -ForegroundColor Green
} else {
    Write-Host "✗ Node.js 未安装" -ForegroundColor Red
    Write-Host ""
    Write-Host "请安装 Node.js:" -ForegroundColor Yellow
    Write-Host "  1. 访问: https://nodejs.org/" -ForegroundColor Cyan
    Write-Host "  2. 下载 LTS 版本（推荐 18.x 或 20.x）" -ForegroundColor Cyan
    Write-Host "  3. 运行安装程序，确保勾选 'Add to PATH'" -ForegroundColor Cyan
    Write-Host "  4. 安装完成后重启终端" -ForegroundColor Cyan
    exit 1
}

# 检查 npm
Write-Host "检查 npm..." -ForegroundColor Yellow
$npmVersion = Get-Command npm -ErrorAction SilentlyContinue
if ($npmVersion) {
    $version = npm --version
    Write-Host "✓ npm 已安装: $version" -ForegroundColor Green
} else {
    Write-Host "✗ npm 未安装" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 检查项目依赖
Write-Host "检查项目依赖..." -ForegroundColor Yellow

# 检查前端依赖
if (Test-Path "frontend/node_modules") {
    Write-Host "✓ 前端依赖已安装" -ForegroundColor Green
} else {
    Write-Host "✗ 前端依赖未安装" -ForegroundColor Yellow
    Write-Host "  执行: cd frontend && npm install" -ForegroundColor Cyan
}

# 检查管理后台依赖
if (Test-Path "admin/node_modules") {
    Write-Host "✓ 管理后台依赖已安装" -ForegroundColor Green
} else {
    Write-Host "✗ 管理后台依赖未安装" -ForegroundColor Yellow
    Write-Host "  执行: cd admin && npm install" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "环境检查完成" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

