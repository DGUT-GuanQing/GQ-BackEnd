# 基础镜像
FROM openjdk:8-jdk-alpine
# 指定工作目录
WORKDIR /app
# 将 jar 包添加到工作目录
ADD target/gq-backend-core-0.0.1-SNAPSHOT.jar .
# 暴露端口
EXPOSE 8086
# 启动命令
ENTRYPOINT ["java","-jar","/app/gq-backend-core-0.0.1-SNAPSHOT.jar"]