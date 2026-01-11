# PMS - 生産管理システム (Production Management System)

生産管理システム（PMS）は、製造業向けの基幹業務システムです。

## 機能概要

- **品目マスタ管理**: 製品・半製品・部品・材料の管理
- **部品構成表（BOM）**: 製品を構成する部品・材料の階層管理
- **工程マスタ管理**: 製造工程の定義と管理
- **工程表管理**: 品目ごとの製造工程順序（ルーティング）
- **取引先マスタ管理**: 仕入先・外注先・顧客の管理
- **単価マスタ管理**: 品目・取引先ごとの単価管理

## 技術スタック

| カテゴリ | 技術 |
|---------|------|
| 言語 | Java 25 |
| フレームワーク | Spring Boot 4.0 |
| テンプレート | Thymeleaf |
| DB アクセス | MyBatis 4.0 |
| データベース | PostgreSQL 16 / H2（デモ） |
| マイグレーション | Flyway |
| ビルド | Gradle (Kotlin DSL) |
| コンテナ | Docker |
| CI/CD | GitHub Actions |

## ディレクトリ構成

```
apps/pms/
├── backend/                    # Spring Boot アプリケーション
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/pms/
│   │   │   │   ├── Application.java
│   │   │   │   └── infrastructure/
│   │   │   │       ├── config/          # 設定クラス
│   │   │   │       └── in/web/          # Web コントローラ
│   │   │   └── resources/
│   │   │       ├── application.yml      # 本番設定
│   │   │       ├── application-demo.yml # デモ設定（H2）
│   │   │       ├── db/
│   │   │       │   ├── migration/       # Flyway マイグレーション
│   │   │       │   └── demo/            # デモ用スキーマ・データ
│   │   │       ├── mapper/              # MyBatis マッパー XML
│   │   │       ├── templates/           # Thymeleaf テンプレート
│   │   │       └── static/              # 静的ファイル
│   │   └── test/
│   ├── build.gradle.kts
│   ├── Dockerfile
│   └── config/                 # 品質チェック設定
│       ├── checkstyle/
│       ├── pmd/
│       └── spotbugs/
├── docker-compose.yml          # 開発用 Docker Compose
└── ops/
    ├── docker/schemaspy/       # SchemaSpy 設定
    └── nix/shell.nix           # Nix 開発環境
```

## セットアップ

### 前提条件

- Java 25
- Docker & Docker Compose
- Node.js 22（Gulp タスク用）

### Nix を使用する場合

```bash
# プロジェクトルートで
nix develop .#pms
```

### 開発環境の起動

```bash
# PostgreSQL コンテナを起動
npm run docker:pms:up

# または Gulp タスクで
gulp docker:pms:up
```

### アプリケーションの起動

```bash
cd apps/pms/backend

# PostgreSQL を使用（デフォルト）
./gradlew bootRun

# H2 インメモリデータベースを使用（デモモード）
./gradlew bootRun --args='--spring.profiles.active=demo'
```

### アクセス URL

| URL | 説明 |
|-----|------|
| http://localhost:8082/ | ホーム画面 |
| http://localhost:8082/swagger-ui.html | API ドキュメント |
| http://localhost:8082/h2-console | H2 Console（デモモードのみ） |

### H2 Console 接続情報

- JDBC URL: `jdbc:h2:mem:pms_demo`
- User Name: `sa`
- Password: (空欄)

## 開発

### ビルド

```bash
cd apps/pms/backend
./gradlew build
```

### テスト

```bash
# 全テスト実行
./gradlew test

# TDD モード（常に実行）
./gradlew tdd
```

### 品質チェック

```bash
# 全品質チェック実行
./gradlew qualityCheck

# テスト + 品質チェック + カバレッジ
./gradlew fullCheck
```

### SchemaSpy（ER 図生成）

```bash
# ER 図を生成
gulp schemaspy:pms:generate

# ER 図を生成してブラウザで開く
gulp schemaspy:pms
```

## データベース

### スキーマ

| テーブル | 説明 |
|---------|------|
| 部門マスタ | 組織の部門情報 |
| 担当者マスタ | 担当者情報 |
| 場所マスタ | 倉庫・工場・ライン |
| 単位マスタ | 数量の単位 |
| 品目マスタ | 製品・半製品・部品・材料 |
| 部品構成表 | BOM（部品表） |
| 工程マスタ | 製造工程 |
| 工程表 | 品目ごとの工程順序 |
| 取引先マスタ | 仕入先・外注先・顧客 |
| 単価マスタ | 品目・取引先ごとの単価 |
| カレンダマスタ | 就業日・休日 |
| 欠点マスタ | 品質不良の種類 |

### 接続情報

| 環境 | URL | ポート |
|------|-----|--------|
| 開発（PostgreSQL） | `jdbc:postgresql://localhost:5434/pms` | 5434 |
| デモ（H2） | `jdbc:h2:mem:pms_demo` | - |

## デプロイ

### Heroku へのデプロイ

```bash
# 初回：Heroku アプリ作成
gulp heroku:pms:create

# デプロイ
gulp heroku:pms:deploy
```

### GitHub Actions

- **CI**: `apps/pms/**` の変更で自動実行
- **デプロイ**: main ブランチへの push で自動デプロイ

## Gulp タスク一覧

```bash
# Docker
gulp docker:pms:up        # PostgreSQL 起動
gulp docker:pms:down      # PostgreSQL 停止
gulp docker:pms:clean     # PostgreSQL 停止 + ボリューム削除
gulp docker:pms:status    # コンテナ状態確認
gulp docker:pms:logs      # ログ表示

# SchemaSpy
gulp schemaspy:pms:generate  # ER 図生成
gulp schemaspy:pms:open      # ER 図をブラウザで開く
gulp schemaspy:pms           # 生成して開く

# Heroku
gulp heroku:pms:create    # アプリ作成
gulp heroku:pms:deploy    # デプロイ
gulp heroku:pms:logs      # ログ表示
gulp heroku:pms:open      # ブラウザで開く
```

## ライセンス

ISC
