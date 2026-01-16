# PMS デモデプロイ手順書

## 概要

本ドキュメントは、生産管理システム（PMS）バックエンドのデモ環境構築とデプロイについて説明します。
デモ環境は H2 インメモリデータベースを使用し、外部データベースなしで動作する自己完結型の構成です。

## デモ環境の特徴

| 項目 | 内容 |
|------|------|
| データベース | H2 インメモリ |
| データ永続性 | なし（再起動でリセット） |
| 初期データ | 起動時に自動投入 |
| 用途 | デモ、プレゼンテーション、機能確認 |
| 外部依存 | なし（PostgreSQL 不要） |
| API ドキュメント | Swagger UI（`/swagger-ui.html`） |

## 技術スタック

- **Java**: 25
- **Spring Boot**: 4.0.0
- **データベース**: H2 Database（インメモリモード）/ PostgreSQL（本番）
- **ORM**: MyBatis
- **マイグレーション**: Flyway
- **ビルドツール**: Gradle (Kotlin DSL)

## ローカル実行

### 前提条件

- JDK 25 以上
- Gradle 9.x（または Gradle Wrapper 使用）

### 起動方法

#### 開発環境（PostgreSQL）

```bash
cd apps/pms/backend
./gradlew bootRun
```

#### デモ環境（H2 インメモリ）

```bash
cd apps/pms/backend
./gradlew bootRun --args='--spring.profiles.active=demo'
```

#### IntelliJ IDEA から起動

1. `Application.java` を右クリック
2. `Run 'Application'` または `Debug 'Application'` を選択
3. 実行構成で VM オプションを追加:
   ```
   -Dspring.profiles.active=demo
   ```

または環境変数で設定:
```
SPRING_PROFILES_ACTIVE=demo
```

### 動作確認

起動後、以下のエンドポイントで動作確認できます。

#### Web アプリケーション

ブラウザで以下にアクセス:
```
http://localhost:8082
```

#### Swagger UI

```
http://localhost:8082/swagger-ui.html
```

**Swagger UI の機能:**
- 全 API エンドポイントの一覧と詳細
- リクエスト/レスポンスのスキーマ確認
- ブラウザから直接 API を実行・テスト

#### H2 コンソール（デモ環境のみ）

ブラウザで以下にアクセス:
```
http://localhost:8082/h2-console
```

**接続情報:**

| 項目 | 値 |
|------|-----|
| JDBC URL | `jdbc:h2:mem:pms_demo` |
| User Name | `sa` |
| Password | (空) |

## デモ環境設定

### application-demo.yml

デモ環境専用の設定ファイルを作成します。

```yaml
spring:
  application:
    name: pms-backend

  # Flyway 自動構成を除外（H2 では不要）
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration

  # H2 インメモリデータベース
  datasource:
    url: jdbc:h2:mem:pms_demo;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: sa
    password:

  # H2 コンソール
  h2:
    console:
      enabled: true
      path: /h2-console
      settings:
        web-allow-others: true

  # SQL 初期化
  sql:
    init:
      mode: always
      schema-locations: classpath:db/demo/schema.sql
      data-locations: classpath:db/demo/data.sql

server:
  port: ${PORT:8082}

mybatis:
  mapper-locations: classpath:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: false

logging:
  level:
    root: INFO
    com.example.pms: INFO
```

### H2 依存関係の追加

`build.gradle.kts` に H2 の依存関係を追加:

```kotlin
// H2 Database（デモ環境用 - implementation でサーブレット登録可能に）
implementation("com.h2database:h2")
```

**重要**: Spring Boot 4.0 では H2 Console サーブレットを明示的に登録する必要があります。
`runtimeOnly` ではなく `implementation` を使用し、`H2ConsoleConfig.java` を作成してください。

### H2ConsoleConfig.java

```java
package com.example.pms.infrastructure.config;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
public class H2ConsoleConfig {

    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2ConsoleServlet() {
        ServletRegistrationBean<JakartaWebServlet> registrationBean =
                new ServletRegistrationBean<>(new JakartaWebServlet());
        registrationBean.addUrlMappings("/h2-console/*");
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }
}
```

## Heroku デプロイ

### 前提条件

- Heroku CLI インストール済み
- Heroku アカウント作成済み
- Docker Desktop インストール済み

### Dockerfile

`apps/pms/backend/Dockerfile` を作成:

```dockerfile
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
ENV SPRING_PROFILES_ACTIVE=demo
CMD java -Dserver.port=${PORT:-8082} -jar app.jar
```

**ビルドコンテキストについて:**

この Dockerfile は `apps/pms/backend/` ディレクトリをビルドコンテキストとして使用します。

### デプロイ手順（Docker コマンド使用）

#### 1. Heroku アプリ作成

```bash
heroku create deploy-demo-pms
```

#### 2. スタックを container に設定

```bash
heroku stack:set container -a deploy-demo-pms
```

#### 3. 環境変数設定

```bash
heroku config:set SPRING_PROFILES_ACTIVE=demo -a deploy-demo-pms
```

#### 4. Heroku Container Registry にログイン

```bash
heroku container:login
```

`Login Succeeded` と表示されれば成功です。

#### 5. Docker ビルド

`apps/pms/backend/` をビルドコンテキストとしてビルドします:

```bash
docker build --platform linux/amd64 --provenance=false -t registry.heroku.com/deploy-demo-pms/web apps/pms/backend
```

#### 6. プッシュ

```bash
docker push registry.heroku.com/deploy-demo-pms/web
```

#### 7. リリース

```bash
heroku container:release web -a deploy-demo-pms
```

#### 8. 動作確認

```bash
# ログ確認
heroku logs --tail -a deploy-demo-pms

# アプリを開く
heroku open -a deploy-demo-pms
```

**Heroku デモ環境 URL:**

| エンドポイント | URL |
|---------------|-----|
| Web アプリ | https://deploy-demo-pms-40869571939f.herokuapp.com/ |
| Swagger UI | https://deploy-demo-pms-40869571939f.herokuapp.com/swagger-ui.html |

### データリセット

Dyno を再起動するとデータがリセットされ、初期データが再投入されます。

```bash
heroku restart -a deploy-demo-pms
```

## GitHub Actions による自動デプロイ

### ワークフローファイル

`.github/workflows/deploy-demo-pms.yml` を作成:

```yaml
name: Deploy PMS Demo to Heroku

on:
  push:
    branches: [ main ]
    paths:
      - 'apps/pms/backend/**'
      - '.github/workflows/deploy-demo-pms.yml'
  workflow_dispatch:

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Install Heroku CLI
        run: curl https://cli-assets.heroku.com/install.sh | sh

      - name: Login to Heroku Container Registry
        env:
          HEROKU_API_KEY: ${{ secrets.HEROKU_API_KEY_PMS }}
        run: heroku container:login

      - name: Build and push
        env:
          HEROKU_API_KEY: ${{ secrets.HEROKU_API_KEY_PMS }}
        run: |
          docker build --platform linux/amd64 --provenance=false -t registry.heroku.com/deploy-demo-pms/web apps/pms/backend
          docker push registry.heroku.com/deploy-demo-pms/web

      - name: Release
        env:
          HEROKU_API_KEY: ${{ secrets.HEROKU_API_KEY_PMS }}
        run: heroku container:release web -a deploy-demo-pms
```

### GitHub Secrets の設定

リポジトリの Settings → Secrets and variables → Actions で以下を登録：

| シークレット名 | 内容 |
|----------------|------|
| `HEROKU_API_KEY_PMS` | Heroku API キー（PMS 用） |

#### Heroku API キーの取得

```bash
heroku auth:token
```

または Heroku ダッシュボード → Account Settings → API Key から取得。

## プロファイル比較

| 項目 | default（開発） | demo（デモ） |
|------|-----------------|--------------|
| データベース | PostgreSQL | H2 インメモリ |
| ポート | 8082 | 8082 |
| マイグレーション | Flyway | SQL init |
| データ永続性 | あり | なし |
| 外部依存 | Docker/PostgreSQL 必要 | 不要 |
| H2 コンソール | 無効 | 有効 |
| 用途 | 開発・テスト | デモ・検証 |

## 各システムとの環境比較

| 項目 | PMS（生産管理） | FAS（財務会計） | SMS（販売管理） |
|------|----------------|-----------------|-----------------|
| アプリケーションポート | 8082 | 8081 | 8080 |
| PostgreSQL ポート | 5434 | 5433 | 5432 |
| データベース名 | pms | fas | sms |
| H2 データベース名 | pms_demo | fas_demo | sms_demo |
| Heroku アプリ名 | deploy-demo-pms | deploy-demo-fas | deploy-demo-sms |

## トラブルシューティング

### H2 コンソールにアクセスできない（404 エラー）

Spring Boot 4.0 では H2 Console サーブレットが自動登録されません。

1. `build.gradle.kts` で H2 を `implementation` に変更（`runtimeOnly` ではなく）
2. `H2ConsoleConfig.java` を作成して `JakartaWebServlet` を明示的に登録
3. `spring.h2.console.enabled=true` が設定されているか確認
4. プロファイルが `demo` になっているか確認

### データが初期化されない

1. `spring.sql.init.mode=always` が設定されているか確認
2. `db/demo/schema.sql` と `db/demo/data.sql` が存在するか確認
3. SQL 構文が H2 互換か確認

### Heroku: no basic auth credentials エラー

Docker が Heroku Container Registry にログインできていません。

```bash
# Heroku CLI 経由で認証
heroku container:login
```

それでも解決しない場合は、手動で認証:

```bash
# API キーを使用して手動ログイン
docker login registry.heroku.com -u _ -p $(heroku auth:token)
```

### Heroku: App crashed (H10 エラー)

起動に失敗しています。ログを確認:

```bash
heroku logs --tail -a deploy-demo-pms
```

よくある原因:
1. `SPRING_PROFILES_ACTIVE=demo` が未設定
2. Dockerfile の CMD が正しくない（`$PORT` を使用しているか確認）
3. Java バージョンの不一致

### Heroku: Docker イメージがプッシュできない

```bash
# platform と provenance オプションを指定
docker build --platform linux/amd64 --provenance=false -t registry.heroku.com/deploy-demo-pms/web apps/pms/backend
```

## 制約事項

| 制約 | 説明 |
|------|------|
| データ非永続 | アプリ再起動でデータはリセット |
| シングルインスタンス | 複数インスタンスでのデータ共有不可 |
| 本番利用不可 | あくまでデモ・検証用途 |
| 大量データ非対応 | インメモリのため大量データには不向き |

## 関連ドキュメント

- [技術スタック](techstack.md) - 使用技術の詳細
- [第22章：生産管理システムの全体像](part4/chapter22.md) - PMS の設計
- [SMS デプロイ手順書](deploy-sms.md) - 販売管理システムのデプロイ
- [FAS デプロイ手順書](deploy-fas.md) - 財務会計システムのデプロイ
- [プロジェクトセットアップ手順書](project-setup-instruction.md) - 新システム追加手順
