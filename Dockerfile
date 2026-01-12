FROM eclipse-temurin:25-jre-alpine AS runtime

WORKDIR /app

# 빌드된 jar 파일 복사
COPY build/libs/porest-0.0.1-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
