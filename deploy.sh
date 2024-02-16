#!/bin/bash

# 停止和删除容器
docker-compose down

mvn clean install package -DskipTests

# 构建并启动容器
docker-compose build --no-cache
docker-compose up -d

