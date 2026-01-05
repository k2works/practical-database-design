# ビルドステージ
FROM gradle:jdk25 AS builder
WORKDIR /app
COPY ./ ./
RUN gradle build -x test --no-daemon

# 実行ステージ
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
CMD ["java", "-Dserver.port=${PORT:-8081}", "-Dspring.profiles.active=demo", "-jar", "app.jar"]
