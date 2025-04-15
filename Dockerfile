FROM openjdk:17
LABEL authors="Captone02"

# JAR 파일 경로 (Gradle 빌드 기준으로 build/libs/*.jar)
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} /app.jar
COPY src/main/resources/application.yml /app/src/main/resources/application.yml

# 환경변수로 Spring 프로파일 설정
ENV SPRING_PROFILES_ACTIVE=prod

# 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "/app.jar"]
