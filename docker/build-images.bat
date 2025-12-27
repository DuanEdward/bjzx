@echo off
REM ============================================
REM Docker 镜像构建脚本 (Windows)
REM 在本地构建所有 Docker 镜像
REM ============================================

setlocal enabledelayedexpansion

echo ============================================
echo 开始构建 Docker 镜像
echo ============================================

REM 获取项目根目录
set "PROJECT_ROOT=%~dp0.."
cd /d "%PROJECT_ROOT%"

REM 检查 Docker 是否安装
where docker >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: Docker 未安装
    echo 请先安装 Docker Desktop: https://www.docker.com/products/docker-desktop
    exit /b 1
)

REM 1. 构建前端镜像
echo [1/3] 构建前端镜像...
cd /d "%PROJECT_ROOT%\frontend"
docker build -t bjzxjj-frontend:latest .
if %errorlevel% neq 0 (
    echo 前端镜像构建失败
    exit /b 1
)
echo ✓ 前端镜像构建完成

REM 2. 构建管理后台镜像
echo [2/3] 构建管理后台镜像...
cd /d "%PROJECT_ROOT%\admin"
docker build -t bjzxjj-admin:latest .
if %errorlevel% neq 0 (
    echo 管理后台镜像构建失败
    exit /b 1
)
echo ✓ 管理后台镜像构建完成

REM 3. 构建后端镜像
echo [3/3] 构建后端镜像...
cd /d "%PROJECT_ROOT%\backend"
docker build -t bjzxjj-backend:latest .
if %errorlevel% neq 0 (
    echo 后端镜像构建失败
    exit /b 1
)
echo ✓ 后端镜像构建完成

echo ============================================
echo 所有镜像构建完成！
echo ============================================
echo.
echo 查看镜像：
docker images | findstr bjzxjj
echo.
echo 下一步：
echo 1. 保存镜像为 tar 文件: docker\save-images.bat
echo 2. 或推送到 Docker Registry
echo 3. 或直接使用 docker-compose: docker-compose up -d

endlocal

