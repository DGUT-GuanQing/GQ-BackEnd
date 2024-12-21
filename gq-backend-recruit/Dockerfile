# 基础镜像
FROM openjdk:8-jdk-alpine
# 指定工作目录
WORKDIR /app
#指定时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
# 创建日志目录
RUN mkdir -p /app/logs
# 将 jar 包添加到工作目录
ADD target/gq-backend-recruit-0.0.1-SNAPSHOT.jar .
# 暴露端口
EXPOSE 8087
# 启动命令
ENTRYPOINT ["sh", "-c", "java -jar /app/gq-backend-recruit-0.0.1-SNAPSHOT.jar > /app/logs/gq-backend-recruit.log 2>&1"]