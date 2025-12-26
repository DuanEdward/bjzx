@echo off
REM 本地构建脚本 - Windows 版本
REM 用于在本地构建前端和管理后台，然后上传到服务器

setlocal enabledelayedexpansion

echo ==========================================
echo 本地构建脚本 (Windows)
echo ==========================================
echo.

REM 检查 Node.js
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未安装 Node.js
    echo 请先安装 Node.js: https://nodejs.org/
    echo 安装后请重新打开命令行窗口
    pause
    exit /b 1
)

REM 检查 npm
where npm >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未安装 npm
    pause
    exit /b 1
)

echo Node.js版本:
node --version
echo npm版本:
npm --version
echo.

REM 获取脚本所在目录
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

REM 1. 构建前端
echo [1/2] 构建前端项目...
cd frontend

if not exist "node_modules" (
    echo 安装前端依赖...
    call npm install
    if !errorlevel! neq 0 (
        echo [错误] 前端依赖安装失败
        pause
        exit /b 1
    )
)

echo 开始构建前端...
call npm run build
if !errorlevel! neq 0 (
    echo [错误] 前端构建失败
    pause
    exit /b 1
)

if not exist "dist" (
    echo [错误] 前端构建失败，未找到 dist 目录
    pause
    exit /b 1
)

echo [成功] 前端构建完成
echo.

REM 2. 构建管理后台
echo [2/2] 构建管理后台...
cd ..\admin

if not exist "node_modules" (
    echo 安装管理后台依赖...
    call npm install
    if !errorlevel! neq 0 (
        echo [错误] 管理后台依赖安装失败
        pause
        exit /b 1
    )
)

echo 开始构建管理后台...
call npm run build
if !errorlevel! neq 0 (
    echo [错误] 管理后台构建失败
    pause
    exit /b 1
)

if not exist "dist" (
    echo [错误] 管理后台构建失败，未找到 dist 目录
    pause
    exit /b 1
)

echo [成功] 管理后台构建完成
echo.

REM 3. 创建构建产物目录
echo 创建构建产物目录...
cd "%SCRIPT_DIR%"

set "BUILD_DIR=build-artifacts"
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"

REM 复制前端构建产物
echo 打包前端构建产物...
xcopy /E /I /Y "frontend\dist" "%BUILD_DIR%\frontend-dist"

REM 复制管理后台构建产物
echo 打包管理后台构建产物...
xcopy /E /I /Y "admin\dist" "%BUILD_DIR%\admin-dist"

REM 创建压缩包（使用 PowerShell）
echo 创建压缩包...
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set "TIMESTAMP=%datetime:~0,8%_%datetime:~8,6%"
set "ARCHIVE_NAME=build-artifacts-%TIMESTAMP%.zip"

powershell -Command "Compress-Archive -Path '%BUILD_DIR%\*' -DestinationPath '%ARCHIVE_NAME%' -Force"

if !errorlevel! neq 0 (
    echo [警告] 压缩包创建失败，但构建产物已准备好
    echo 构建产物目录: %BUILD_DIR%
) else (
    echo [成功] 构建产物已打包: %ARCHIVE_NAME%
    
    REM 清理临时目录
    rmdir /s /q "%BUILD_DIR%"
)

echo.
echo ==========================================
echo 构建完成！
echo ==========================================
echo.
if exist "%ARCHIVE_NAME%" (
    echo 构建产物: %ARCHIVE_NAME%
    echo.
    echo 上传到服务器后，执行以下命令解压和部署：
    echo   unzip %ARCHIVE_NAME%
    echo   cp -r frontend-dist/* /home/bjzxjj/frontend/
    echo   cp -r admin-dist/* /home/bjzxjj/admin/
) else (
    echo 构建产物目录: %BUILD_DIR%
    echo 请手动打包此目录后上传到服务器
)
echo.
pause

