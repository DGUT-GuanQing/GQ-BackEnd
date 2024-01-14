#!/bin/bash

# 停止和删除容器
docker-compose down

# 清除并重新打包 common 依赖
cd /opt/gq-backend/gq-backend-common
mvn clean install
cd ..

# 对每个服务执行清除和打包
# gq-backend-gateway
cd /opt/gq-backend/gq-backend-gateway
mvn clean package -DskipTests
cd ..

# gq-backend-core
cd /opt/gq-backend/gq-backend-core
mvn clean package -DskipTests
cd ..

# gq-backend-recruit
cd /opt/gq-backend/gq-backend-recruit
mvn clean package -DskipTests
cd ..

# gq-backend-admin
cd /opt/gq-backend/gq-backend-admin
mvn clean package -DskipTests
cd ..

# 构建并启动容器
docker-compose build --no-cache
docker-compose up -d

