@echo off
REM ============================================
REM 保存 Docker 镜像为 tar 文件 (Windows)
REM 用于传输到服务器
REM ============================================

setlocal enabledelayedexpansion

echo ============================================
echo 保存 Docker 镜像
echo ============================================

REM 创建输出目录
set "OUTPUT_DIR=%~dp0images"
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

REM 保存镜像
set "IMAGES=bjzxjj-frontend:latest bjzxjj-admin:latest bjzxjj-backend:latest"

for %%I in (%IMAGES%) do (
    echo 保存镜像: %%I
    set "FILENAME=%%I"
    set "FILENAME=!FILENAME::=_!"
    set "FILENAME=!FILENAME:/=_!"
    docker save %%I | gzip > "%OUTPUT_DIR%\!FILENAME!.tar.gz"
    if !errorlevel! equ 0 (
        echo ✓ 已保存: %OUTPUT_DIR%\!FILENAME!.tar.gz
    ) else (
        echo ✗ 保存失败: %%I
    )
)

echo ============================================
echo 所有镜像已保存！
echo ============================================
echo.
echo 文件大小：
for %%F in ("%OUTPUT_DIR%\*.tar.gz") do (
    echo   %%~zF bytes - %%~nxF
)
echo.
echo 下一步：
echo 1. 将 %OUTPUT_DIR% 目录上传到服务器
echo 2. 在服务器上运行: ./docker/load-images.sh

endlocal

