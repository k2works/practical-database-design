# ビルドステージ
FROM gradle:jdk25 AS builder
WORKDIR /app
COPY ./ ./
RUN gradle build -x test --no-daemon

# 実行ステージ
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080

# 環境変数でプロファイルを制御（デフォルトは demo）
ENV SPRING_PROFILES_ACTIVE=demo
ENV PORT=8080

CMD ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
