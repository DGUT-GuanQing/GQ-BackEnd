#!/bin/bash

# 停止和删除容器
docker-compose down

# 清除Maven依赖并重新打包（忽略测试）
mvn clean package -DskipTests

# 构建并启动容器
docker-compose up -d
