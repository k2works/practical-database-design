# SMS - 販売管理システム (Sales Management System)

販売管理システム（SMS）は、販売業務を支援する基幹業務システムです。

## 機能概要

- **部門マスタ管理**: 組織の部門情報管理
- **社員マスタ管理**: 社員情報・担当者管理
- **商品マスタ管理**: 商品・製品情報管理
- **取引先マスタ管理**: 顧客・仕入先情報管理
- **見積管理**: 見積書の作成・管理
- **受注管理**: 受注情報の登録・管理
- **出荷管理**: 出荷指示・実績管理
- **売上管理**: 売上計上・管理
- **請求管理**: 請求書発行・管理
- **入金管理**: 入金情報の登録・管理
- **発注管理**: 発注情報の登録・管理
- **入荷管理**: 入荷・検収管理
- **在庫管理**: 在庫数量・移動管理
- **支払管理**: 支払情報の登録・管理

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
apps/sms/
├── backend/                    # Spring Boot アプリケーション
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/sms/
│   │   │   │   ├── Application.java
│   │   │   │   ├── domain/              # ドメイン層
│   │   │   │   ├── application/         # アプリケーション層
│   │   │   │   └── infrastructure/      # インフラ層
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
nix develop .#sms
```

### 開発環境の起動

```bash
# PostgreSQL コンテナを起動
npm run docker:sms:up

# または Gulp タスクで
gulp docker:sms:up
```

### アプリケーションの起動

```bash
cd apps/sms/backend

# PostgreSQL を使用（デフォルト）
./gradlew bootRun

# H2 インメモリデータベースを使用（デモモード）
./gradlew bootRun --args='--spring.profiles.active=demo'
```

### アクセス URL

| URL | 説明 |
|-----|------|
| http://localhost:8080/ | ホーム画面 |
| http://localhost:8080/swagger-ui.html | API ドキュメント |
| http://localhost:8080/h2-console | H2 Console（デモモードのみ） |

### H2 Console 接続情報

- JDBC URL: `jdbc:h2:mem:sms_demo`
- User Name: `sa`
- Password: (空欄)

## 開発

### ビルド

```bash
cd apps/sms/backend
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
gulp schemaspy:sms:generate

# ER 図を生成してブラウザで開く
gulp schemaspy:sms
```

## データベース

### スキーマ

| テーブル | 説明 |
|---------|------|
| 部門マスタ | 組織の部門情報 |
| 社員マスタ | 社員・担当者情報 |
| 商品マスタ | 商品・製品情報 |
| 顧客別単価 | 顧客ごとの商品単価 |
| 取引先マスタ | 顧客・仕入先情報 |
| 見積ヘッダ/明細 | 見積情報 |
| 受注ヘッダ/明細 | 受注情報 |
| 出荷ヘッダ/明細 | 出荷情報 |
| 売上ヘッダ/明細 | 売上情報 |
| 請求ヘッダ/明細 | 請求情報 |
| 入金ヘッダ/明細 | 入金情報 |
| 発注ヘッダ/明細 | 発注情報 |
| 入荷ヘッダ/明細 | 入荷情報 |
| 在庫マスタ | 在庫数量 |
| 在庫移動履歴 | 在庫移動記録 |
| 支払ヘッダ/明細 | 支払情報 |
| 赤黒テーブル | 訂正仕訳 |
| 採番テーブル | 伝票番号採番 |
| 履歴テーブル | 変更履歴 |

### 接続情報

| 環境 | URL | ポート |
|------|-----|--------|
| 開発（PostgreSQL） | `jdbc:postgresql://localhost:5432/sms` | 5432 |
| デモ（H2） | `jdbc:h2:mem:sms_demo` | - |

## デプロイ

### Heroku へのデプロイ

```bash
# 初回：Heroku アプリ作成
gulp heroku:sms:create

# デプロイ
gulp heroku:sms:deploy
```

### GitHub Actions

- **CI**: `apps/sms/**` の変更で自動実行
- **デプロイ**: main ブランチへの push で自動デプロイ

## Gulp タスク一覧

```bash
# Docker
gulp docker:sms:up        # PostgreSQL 起動
gulp docker:sms:down      # PostgreSQL 停止
gulp docker:sms:clean     # PostgreSQL 停止 + ボリューム削除
gulp docker:sms:status    # コンテナ状態確認
gulp docker:sms:logs      # ログ表示

# SchemaSpy
gulp schemaspy:sms:generate  # ER 図生成
gulp schemaspy:sms:open      # ER 図をブラウザで開く
gulp schemaspy:sms           # 生成して開く

# Heroku
gulp heroku:sms:create    # アプリ作成
gulp heroku:sms:deploy    # デプロイ
gulp heroku:sms:logs      # ログ表示
gulp heroku:sms:open      # ブラウザで開く

# テスト
gulp test:sms:backend     # Backend テスト実行
```

## ライセンス

ISC
