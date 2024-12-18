#!/bin/bash

# 停止和删除容器
docker-compose down

mvn clean install package -DskipTests

# 构建并启动容器
docker-compose build --no-cache
docker-compose up -d

# 清理无标签的镜像
docker rmi $(docker images -f "dangling=true" -q)

