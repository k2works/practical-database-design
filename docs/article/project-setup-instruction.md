# プロジェクトセットアップ手順書

本ドキュメントは、新しいサブシステム（アプリケーション）を追加する際の手順を再利用可能な形式でまとめたものです。

## 概要

本プロジェクトでは、以下の 3 つのサブシステムを同一構成で管理しています：

| システム | ポート | ディレクトリ |
|---------|--------|-------------|
| SMS（販売管理システム） | 8080 | `apps/sms/` |
| FAS（財務会計システム） | 8081 | `apps/fas/` |
| PMS（生産管理システム） | 8082 | `apps/pms/` |

新しいサブシステムを追加する場合は、既存のシステム（FAS または PMS）をテンプレートとして使用します。

---

## 前提条件

### 必須ツール

- Java 25（Nix 環境で提供）
- Docker & Docker Compose
- Node.js 22（Gulp タスク用）
- Heroku CLI（デプロイ用）
- GitHub CLI（gh）

### 推奨ツール

- Nix（開発環境の統一管理）
- IntelliJ IDEA または VS Code

---

## Phase 1: アプリケーション基盤の作成

### 1.1 ディレクトリ構造の作成

```bash
# 新システム名を定義（例: XMS）
export NEW_SYSTEM=xms
export NEW_SYSTEM_UPPER=XMS
export NEW_PORT=8083
export NEW_PG_PORT=5435

# 既存システムをコピー
cp -r apps/pms apps/${NEW_SYSTEM}

# ディレクトリ構造
apps/${NEW_SYSTEM}/
├── backend/                    # Spring Boot アプリケーション
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/${NEW_SYSTEM}/
│   │   │   └── resources/
│   │   └── test/
│   ├── build.gradle.kts
│   ├── Dockerfile
│   └── config/                 # 品質チェック設定
├── docker-compose.yml
├── README.md
└── ops/
    ├── docker/schemaspy/
    └── nix/shell.nix
```

### 1.2 パッケージ名・設定の変更

#### build.gradle.kts

```kotlin
plugins {
    java
    jacoco
    checkstyle
    pmd
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.spotbugs") version "6.0.27"
}

group = "com.example.${NEW_SYSTEM}"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

// バージョン管理
val mybatisVersion = "4.0.0"
val testcontainersVersion = "1.20.4"
val springdocVersion = "2.8.3"
val thymeleafLayoutDialectVersion = "3.4.0"
val poiVersion = "5.3.0"
val openhtmltopdfVersion = "1.1.36"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:$thymeleafLayoutDialectVersion")

    // Webjars
    implementation("org.webjars:bootstrap:5.3.3")
    implementation("org.webjars:webjars-locator-core:0.59")

    // 帳票出力
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("io.github.openhtmltopdf:openhtmltopdf-pdfbox:$openhtmltopdfVersion")

    // OpenAPI/Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    // Database
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:$mybatisVersion")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")

    // H2 Database（デモ環境用 - implementation でサーブレット登録可能に）
    implementation("com.h2database:h2")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:$mybatisVersion")
    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// JaCoCo
jacoco {
    toolVersion = "0.8.14" // Java 25 support
}

// Checkstyle
checkstyle {
    toolVersion = "10.20.2"
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
}

// SpotBugs (Java 25 対応: 4.9.7+)
spotbugs {
    ignoreFailures = false
    excludeFilter = file("${rootDir}/config/spotbugs/exclude.xml")
    toolVersion = "4.9.8"
}

// PMD (Java 25 対応: 7.16.0+)
pmd {
    toolVersion = "7.16.0"
    isConsoleOutput = true
    ruleSetFiles = files("${rootDir}/config/pmd/ruleset.xml")
    ruleSets = listOf()
    isIgnoreFailures = false
}

// カスタムタスク
tasks.register<Test>("tdd") { /* TDD用 */ }
tasks.register("qualityCheck") { /* 品質チェック全実行 */ }
tasks.register("fullCheck") { /* 全テストと品質チェック */ }
```

#### application.yml

```yaml
spring:
  application:
    name: ${NEW_SYSTEM}
  datasource:
    url: jdbc:postgresql://localhost:${NEW_PG_PORT}/${NEW_SYSTEM}
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
  h2:
    console:
      enabled: false

server:
  port: ${NEW_PORT}

mybatis:
  mapper-locations: classpath:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
```

#### application-demo.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:${NEW_SYSTEM}_demo;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
  flyway:
    enabled: false
  sql:
    init:
      mode: always
      schema-locations: classpath:db/demo/schema.sql
      data-locations: classpath:db/demo/data.sql
  h2:
    console:
      enabled: true
      path: /h2-console
      settings:
        web-allow-others: true
```

### 1.3 H2 Console 設定（Spring Boot 4.0 対応）

**重要**: Spring Boot 4.0 では H2 Console サーブレットを明示的に登録する必要があります。

#### H2ConsoleConfig.java

```java
package com.example.${NEW_SYSTEM}.infrastructure.config;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * H2 Console 設定クラス.
 * Spring Boot 4.0 で H2 Console サーブレットを明示的に登録する.
 */
@Configuration
@ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
public class H2ConsoleConfig {

    /**
     * H2 Console サーブレットを登録.
     *
     * @return ServletRegistrationBean
     */
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

**注意**: この設定を有効にするには、`build.gradle.kts` で H2 を `implementation` として追加する必要があります（`runtimeOnly` ではなく）。

```kotlin
// ❌ これではサーブレット登録不可
runtimeOnly("com.h2database:h2")

// ✅ これならサーブレット登録可能
implementation("com.h2database:h2")
```

### 1.4 MyBatis databaseId 設定

H2 と PostgreSQL の両方に対応するための設定です。

#### MyBatisConfig.java

```java
package com.example.${NEW_SYSTEM}.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import java.util.Properties;

@Configuration
public class MyBatisConfig {

    @Bean
    public String databaseId(DataSource dataSource) {
        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("PostgreSQL", "postgresql");
        properties.setProperty("H2", "h2");
        databaseIdProvider.setProperties(properties);
        try {
            String productName = JdbcUtils.extractDatabaseMetaData(
                    dataSource, "getDatabaseProductName");
            return databaseIdProvider.getDatabaseId(dataSource);
        } catch (MetaDataAccessException e) {
            throw new RuntimeException("Failed to determine database type", e);
        }
    }
}
```

### 1.5 Dockerfile

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
EXPOSE ${NEW_PORT}

# デフォルトは demo プロファイル（Heroku 用）
ENV SPRING_PROFILES_ACTIVE=demo
CMD java -Dserver.port=${PORT:-${NEW_PORT}} -jar app.jar
```

### 1.6 docker-compose.yml

```yaml
services:
  postgres:
    image: postgres:16
    container_name: ${NEW_SYSTEM}-postgres
    ports:
      - "${NEW_PG_PORT}:5432"
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: ${NEW_SYSTEM}
    volumes:
      - ${NEW_SYSTEM}_postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: ${NEW_SYSTEM}-backend
    ports:
      - "${NEW_PORT}:${NEW_PORT}"
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${NEW_SYSTEM}
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
    depends_on:
      postgres:
        condition: service_healthy

  schemaspy:
    build:
      context: ./ops/docker/schemaspy
      dockerfile: Dockerfile
    container_name: ${NEW_SYSTEM}-schemaspy
    volumes:
      - ../docs/assets/schemaspy-output/${NEW_SYSTEM}:/output
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      DATABASE_HOST: postgres
      DATABASE_PORT: 5432
      DATABASE_NAME: ${NEW_SYSTEM}
      DATABASE_USER: postgres
      DATABASE_PASSWORD: postgres

volumes:
  ${NEW_SYSTEM}_postgres_data:
```

---

## Phase 2: 開発インフラの設定

### 2.1 Nix 開発環境

#### apps/${NEW_SYSTEM}/ops/nix/shell.nix

```nix
{ packages ? import <nixpkgs> {} }:

packages.mkShell {
  name = "${NEW_SYSTEM}-dev";

  buildInputs = with packages; [
    # Java
    jdk25
    gradle

    # Database
    postgresql_16

    # Tools
    docker
    docker-compose
  ];

  shellHook = ''
    echo "🚀 ${NEW_SYSTEM_UPPER} Development Environment"
    echo "Java: $(java -version 2>&1 | head -1)"
    echo "Gradle: $(gradle --version | grep Gradle)"
    echo ""
    echo "Available commands:"
    echo "  cd apps/${NEW_SYSTEM}/backend && ./gradlew bootRun"
    echo "  cd apps/${NEW_SYSTEM}/backend && ./gradlew bootRun --args='--spring.profiles.active=demo'"
  '';
}
```

#### flake.nix に追加

```nix
devShells = {
  # ... 既存の設定 ...
  ${NEW_SYSTEM} = import ./apps/${NEW_SYSTEM}/ops/nix/shell.nix { inherit packages; };
};
```

### 2.2 Gulp タスクの追加

#### ops/scripts/docker-${NEW_SYSTEM}.js

既存の `docker-pms.js` をコピーして、以下を置換：
- `pms` → `${NEW_SYSTEM}`
- `5434` → `${NEW_PG_PORT}`

#### ops/scripts/schemaspy-${NEW_SYSTEM}.js

既存の `schemaspy-pms.js` をコピーして置換。

#### ops/scripts/heroku-${NEW_SYSTEM}.js

既存の `heroku-pms.js` をコピーして置換。

#### ops/scripts/test.js に追加

```javascript
const ${NEW_SYSTEM_UPPER}_BACKEND_DIR = path.join(process.cwd(), 'apps', '${NEW_SYSTEM}', 'backend');

gulp.task('test:${NEW_SYSTEM}:backend', (done) => {
    try {
        console.log('Running ${NEW_SYSTEM_UPPER} Backend tests...');
        const cmd = process.platform === 'win32' ? 'gradlew.bat test' : 'sh gradlew test';
        execSync(cmd, {
            cwd: ${NEW_SYSTEM_UPPER}_BACKEND_DIR,
            stdio: 'inherit'
        });
        done();
    } catch (error) {
        console.error('${NEW_SYSTEM_UPPER} Backend tests failed');
        done(error);
    }
});

// test:backend に追加
gulp.task('test:backend', gulp.parallel(
    'test:sms:backend',
    'test:fas:backend',
    'test:pms:backend',
    'test:${NEW_SYSTEM}:backend'
));
```

#### gulpfile.js に追加

```javascript
import ${NEW_SYSTEM}DockerTasks from './ops/scripts/docker-${NEW_SYSTEM}.js';
import ${NEW_SYSTEM}SchemaspyTasks from './ops/scripts/schemaspy-${NEW_SYSTEM}.js';
import ${NEW_SYSTEM}HerokuTasks from './ops/scripts/heroku-${NEW_SYSTEM}.js';

${NEW_SYSTEM}DockerTasks(gulp);
${NEW_SYSTEM}SchemaspyTasks(gulp);
${NEW_SYSTEM}HerokuTasks(gulp);

// dev タスクに追加
export const dev = gulp.series(
    gulp.parallel('mkdocs:serve', 'mkdocs:open'),
    gulp.parallel('docker:sms:up', 'docker:fas:up', 'docker:pms:up', 'docker:${NEW_SYSTEM}:up'),
    gulp.parallel('schemaspy:sms:generate', 'schemaspy:fas:generate', 'schemaspy:pms:generate', 'schemaspy:${NEW_SYSTEM}:generate')
);
```

### 2.3 pre-commit フックの設定

#### .husky/pre-commit に追加

```bash
npx lint-staged --config .lintstagedrc.${NEW_SYSTEM}.json
```

#### .lintstagedrc.${NEW_SYSTEM}.json

```json
{
  "apps/${NEW_SYSTEM}/backend/src/**/*.java": [
    "bash -c 'cd apps/${NEW_SYSTEM}/backend && ./gradlew checkstyleMain checkstyleTest pmdMain spotbugsMain --no-daemon'"
  ]
}
```

---

## Phase 3: CI/CD の設定

### 3.1 GitHub Actions CI ワークフロー

#### .github/workflows/ci-${NEW_SYSTEM}.yml

```yaml
name: ${NEW_SYSTEM_UPPER} CI

on:
  push:
    branches: [ main ]
    paths:
      - 'apps/${NEW_SYSTEM}/**'
      - '.github/workflows/ci-${NEW_SYSTEM}.yml'
      - 'flake.nix'
      - 'flake.lock'
  pull_request:
    branches: [ main ]
    paths:
      - 'apps/${NEW_SYSTEM}/**'
      - '.github/workflows/ci-${NEW_SYSTEM}.yml'
      - 'flake.nix'
      - 'flake.lock'
  workflow_dispatch:

jobs:
  backend-check:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Install Nix
        uses: cachix/install-nix-action@v30
        with:
          nix_path: nixpkgs=channel:nixos-unstable
          extra_nix_config: |
            experimental-features = nix-command flakes

      - name: Cache Nix store
        uses: actions/cache@v4
        with:
          path: /nix/store
          key: nix-${{ runner.os }}-${{ hashFiles('flake.lock') }}
          restore-keys: |
            nix-${{ runner.os }}-

      - name: Run Quality Check and Test
        run: nix develop .#${NEW_SYSTEM} --command bash -c "cd apps/${NEW_SYSTEM}/backend && ./gradlew fullCheck"

      - name: Upload Test Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: ${NEW_SYSTEM}-backend-test-report
          path: apps/${NEW_SYSTEM}/backend/build/reports/tests/test

      - name: Upload JaCoCo Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: ${NEW_SYSTEM}-backend-jacoco-report
          path: apps/${NEW_SYSTEM}/backend/build/reports/jacoco/test/html
```

### 3.2 Heroku デプロイワークフロー

#### .github/workflows/deploy-demo-${NEW_SYSTEM}.yml

```yaml
name: Deploy ${NEW_SYSTEM_UPPER} Demo to Heroku

on:
  push:
    branches: [ main ]
    paths:
      - 'apps/${NEW_SYSTEM}/backend/**'
      - '.github/workflows/deploy-demo-${NEW_SYSTEM}.yml'
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
          HEROKU_API_KEY: ${{ secrets.HEROKU_API_KEY_${NEW_SYSTEM_UPPER} }}
        run: heroku container:login

      - name: Build and push
        env:
          HEROKU_API_KEY: ${{ secrets.HEROKU_API_KEY_${NEW_SYSTEM_UPPER} }}
        run: |
          docker build --platform linux/amd64 --provenance=false -t registry.heroku.com/deploy-demo-${NEW_SYSTEM}/web apps/${NEW_SYSTEM}/backend
          docker push registry.heroku.com/deploy-demo-${NEW_SYSTEM}/web

      - name: Release
        env:
          HEROKU_API_KEY: ${{ secrets.HEROKU_API_KEY_${NEW_SYSTEM_UPPER} }}
        run: heroku container:release web -a deploy-demo-${NEW_SYSTEM}
```

---

## Phase 4: Heroku デプロイ

### 4.1 Heroku アプリの作成

```bash
# Heroku CLI でログイン
heroku login

# アプリ作成
heroku create deploy-demo-${NEW_SYSTEM}

# PostgreSQL アドオン追加（本番環境用、オプション）
# heroku addons:create heroku-postgresql:essential-0 -a deploy-demo-${NEW_SYSTEM}

# Container Registry ログイン
heroku container:login

# イメージをビルドしてプッシュ
docker build --platform linux/amd64 --provenance=false \
  -t registry.heroku.com/deploy-demo-${NEW_SYSTEM}/web \
  apps/${NEW_SYSTEM}/backend

docker push registry.heroku.com/deploy-demo-${NEW_SYSTEM}/web

# リリース
heroku container:release web -a deploy-demo-${NEW_SYSTEM}

# ログ確認
heroku logs --tail -a deploy-demo-${NEW_SYSTEM}
```

### 4.2 GitHub Secrets の設定

1. Heroku API Key を取得:
   ```bash
   heroku auth:token
   ```

2. GitHub リポジトリの Settings > Secrets and variables > Actions に追加:
   - Name: `HEROKU_API_KEY_${NEW_SYSTEM_UPPER}`
   - Value: 取得した API Key

---

## Phase 5: ドキュメント更新

### 5.1 README.md の更新

プロジェクトルートの README.md に以下を追加：

- 進捗表に新システムを追加
- デモリンクを追加
- ディレクトリ構成に追加
- Gulp タスク一覧に追加
- Nix 環境設定に追加

### 5.2 docs/index.md の更新

```markdown
- [${NEW_SYSTEM_UPPER}デモ](https://deploy-demo-${NEW_SYSTEM}-XXXXX.herokuapp.com/){:target="_blank"}
- [SchemaSpy ER 図（${NEW_SYSTEM_UPPER}）](./assets/schemaspy-output/${NEW_SYSTEM}/index.html){:target="_blank"}
```

### 5.3 apps/${NEW_SYSTEM}/README.md の作成

既存の `apps/pms/README.md` をテンプレートとして、システム固有の情報に書き換えます。

---

## チェックリスト

### アプリケーション基盤
- [ ] ディレクトリ構造の作成
- [ ] build.gradle.kts の設定
- [ ] application.yml の設定
- [ ] application-demo.yml の設定
- [ ] H2ConsoleConfig.java の作成
- [ ] MyBatisConfig.java の作成
- [ ] Dockerfile の作成
- [ ] docker-compose.yml の作成
- [ ] スキーマファイル（Flyway migration, demo schema/data）

### 開発インフラ
- [ ] Nix shell.nix の作成
- [ ] flake.nix への追加
- [ ] Gulp タスク（docker, schemaspy, heroku）
- [ ] gulpfile.js への登録
- [ ] test.js への追加
- [ ] pre-commit フック設定
- [ ] lint-staged 設定

### CI/CD
- [ ] GitHub Actions CI ワークフロー
- [ ] GitHub Actions デプロイワークフロー
- [ ] GitHub Secrets の設定

### Heroku
- [ ] Heroku アプリの作成
- [ ] Container Registry へのプッシュ
- [ ] リリース実行
- [ ] 動作確認

### ドキュメント
- [ ] README.md の更新
- [ ] docs/index.md の更新
- [ ] apps/${NEW_SYSTEM}/README.md の作成

---

## トラブルシューティング

### H2 Console が 404 エラーになる

**症状**: `/h2-console` にアクセスすると "No static resource h2-console" エラー

**原因**: Spring Boot 4.0 では H2 Console サーブレットが自動登録されない

**解決策**:
1. `build.gradle.kts` で H2 を `implementation` に変更
2. `H2ConsoleConfig.java` を作成して `JakartaWebServlet` を明示的に登録

### Heroku デプロイで Docker イメージがプッシュできない

**症状**: `docker push` が失敗する

**解決策**:
```bash
# platform と provenance オプションを指定
docker build --platform linux/amd64 --provenance=false \
  -t registry.heroku.com/deploy-demo-${NEW_SYSTEM}/web \
  apps/${NEW_SYSTEM}/backend
```

### GitHub Actions で Nix キャッシュが効かない

**解決策**: `flake.lock` のハッシュをキーに含める
```yaml
key: nix-${{ runner.os }}-${{ hashFiles('flake.lock') }}
```

---

## 参考リンク

- [Spring Boot 4.0 Reference](https://docs.spring.io/spring-boot/docs/4.0.0/reference/html/)
- [MyBatis 4.0 Documentation](https://mybatis.org/mybatis-3/)
- [Heroku Container Registry](https://devcenter.heroku.com/articles/container-registry-and-runtime)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Nix Flakes](https://nixos.wiki/wiki/Flakes)
