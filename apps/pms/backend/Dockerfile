# ビルドステージ
FROM gradle:jdk25 AS builder
WORKDIR /app
COPY ./ ./
RUN gradle build -x test --no-daemon

# 実行ステージ
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8082

# デフォルトは demo プロファイル（Heroku 用）
# docker-compose では SPRING_PROFILES_ACTIVE=default で上書き
ENV SPRING_PROFILES_ACTIVE=demo
CMD java -Dserver.port=${PORT:-8082} -jar app.jar
