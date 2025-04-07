FROM openjdk:17-jdk-slim

# JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 컨테이너 시작 시 실행할 명령어
ENTRYPOINT ["java","-jar","/app.jar"]