# Dockerfile

# jdk21 Image Start
FROM eclipse-temurin:21-jdk

# 인자 설정 - JAR_File
ARG JAR_FILE=build/libs/*.jar

# jar 파일 복제
COPY ${JAR_FILE} app.jar

# 포트 지정
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]