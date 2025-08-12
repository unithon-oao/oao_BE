FROM openjdk:17-jdk-slim

# dockerize 설치
RUN apt-get update && apt-get install -y wget \
    && wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz \
    && tar -xvzf dockerize-linux-amd64-v0.6.1.tar.gz \
    && mv dockerize /usr/local/bin/

# 애플리케이션 JAR 파일 복사
COPY build/libs/*SNAPSHOT.jar app.jar

# 서버 실행
ENTRYPOINT ["dockerize", "-wait", "tcp://mysql:3306", "-timeout", "60s", "java", "-jar", "/app.jar"]
