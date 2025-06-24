@echo off
echo 启动宿舍管理系统...
echo.

echo 1. 启动后端服务...
cd springboot
start "Spring Boot Backend" cmd /k "mvn spring-boot:run"
cd ..

echo 2. 启动前端服务...
cd vue

REM 检查 node_modules 目录是否存在，如果不存在则运行 npm install
IF NOT EXIST node_modules (
    echo.
    echo Frontend dependencies not found. Running npm install...
    echo This might take a few minutes.
    echo.
    npm install
)

start "Vue Frontend" cmd /k "npm run serve"
cd ..

echo.
echo 系统启动中，请稍候...
echo 后端地址: http://localhost:9090
echo 前端地址: http://localhost:8080
echo.
echo 按任意键退出...
pause > nul 