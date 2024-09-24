# STAGE 1: 테스트 및 빌드 환경
FROM gradle:7.5.1-jdk17 AS build

# 1-1. 작업 디렉토리 설정
WORKDIR /app

# 1-2. 소스 복사
COPY --chown=gradle:gradle . .

# 1-3. 의존성 설치 (의존성 캐싱)
RUN gradle dependencies --no-daemon

# 1-4. 테스트 및 빌드 실행
RUN gradle clean test build --no-daemon

# STAGE 2: 실행 환경
FROM openjdk:17-jdk-slim

LABEL authors="sangjun"
LABEL description="Woohakdong Server Image"
LABEL version="0.0.1"

# 2-1. 작업 디렉토리 설정
WORKDIR /app

# 2-2. 빌드된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 2-3. 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
