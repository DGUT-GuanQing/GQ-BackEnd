# 基础镜像
FROM openjdk:8-jdk-alpine
# 指定工作目录
WORKDIR /app
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
# 将 jar 包添加到工作目录
ADD target/gq-backend-admin-0.0.1-SNAPSHOT.jar .
# 暴露端口
EXPOSE 8088
# 启动命令
ENTRYPOINT ["java","-jar","/app/gq-backen1d-admin-0.0.1-SNAPSHOT.jar"]