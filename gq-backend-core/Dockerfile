# 基础镜像
FROM openjdk:8-jdk-alpine
# 指定工作目录
WORKDIR /app
#指定时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
# 将 jar 包添加到工作目录
ADD target/gq-backend-core-0.0.1-SNAPSHOT.jar .
# 暴露端口
EXPOSE 8086
RUN mkdir -p /app/logs
# 启动命令
ENTRYPOINT ["sh", "-c", "java -jar /app/gq-backend-core-0.0.1-SNAPSHOT.jar > /app/logs/gq-backend-core.log 2>&1"]