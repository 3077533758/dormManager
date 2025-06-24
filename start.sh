#!/bin/bash

echo "启动宿舍管理系统..."
echo

echo "1. 启动后端服务..."
cd springboot
gnome-terminal --title="Spring Boot Backend" -- bash -c "mvn spring-boot:run; exec bash" &
cd ..

echo "2. 启动前端服务..."
cd vue

# 检查 node_modules 目录是否存在，如果不存在则运行 npm install
if [ ! -d "node_modules" ]; then
    echo "Frontend dependencies not found. Running npm install..."
    echo "This might take a few minutes."
    npm install
fi

gnome-terminal --title="Vue Frontend" -- bash -c "npm run serve; exec bash" &
cd ..

echo
echo "系统启动中，请稍候..."
echo "后端地址: http://localhost:9090"
echo "前端地址: http://localhost:8080"
echo
echo "按任意键退出..."
read -n 1 