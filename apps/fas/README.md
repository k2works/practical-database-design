# FAS - 財務会計システム (Financial Accounting System)

財務会計システム（FAS）は、会計業務を支援する基幹業務システムです。

## 機能概要

- **勘定科目マスタ管理**: 勘定科目の登録・管理
- **勘定科目体系管理**: 勘定科目の階層構造管理
- **部門マスタ管理**: 組織の部門情報管理
- **仕訳入力**: 仕訳伝票の登録・編集
- **自動仕訳**: 定型仕訳の自動生成
- **勘定科目残高管理**: 日次・月次残高の集計
- **赤黒処理**: 訂正仕訳の管理
- **変更履歴管理**: 仕訳変更の追跡

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
apps/fas/
├── backend/                    # Spring Boot アプリケーション
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/fas/
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
nix develop .#fas
```

### 開発環境の起動

```bash
# PostgreSQL コンテナを起動
npm run docker:fas:up

# または Gulp タスクで
gulp docker:fas:up
```

### アプリケーションの起動

```bash
cd apps/fas/backend

# PostgreSQL を使用（デフォルト）
./gradlew bootRun

# H2 インメモリデータベースを使用（デモモード）
./gradlew bootRun --args='--spring.profiles.active=demo'
```

### アクセス URL

| URL | 説明 |
|-----|------|
| http://localhost:8081/ | ホーム画面 |
| http://localhost:8081/swagger-ui.html | API ドキュメント |
| http://localhost:8081/h2-console | H2 Console（デモモードのみ） |

### H2 Console 接続情報

- JDBC URL: `jdbc:h2:mem:fas_demo`
- User Name: `sa`
- Password: (空欄)

## 開発

### ビルド

```bash
cd apps/fas/backend
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
gulp schemaspy:fas:generate

# ER 図を生成してブラウザで開く
gulp schemaspy:fas
```

## データベース

### スキーマ

| テーブル | 説明 |
|---------|------|
| 消費税取引区分マスタ | 消費税の取引区分 |
| 勘定科目マスタ | 勘定科目情報 |
| 勘定科目体系 | 勘定科目の階層構造 |
| 部門マスタ | 組織の部門情報 |
| 仕訳ヘッダ | 仕訳伝票ヘッダ |
| 仕訳明細 | 仕訳伝票明細 |
| 自動仕訳マスタ | 自動仕訳の定義 |
| 自動仕訳明細 | 自動仕訳の明細定義 |
| 日次勘定科目残高 | 日ごとの残高 |
| 月次勘定科目残高 | 月ごとの残高 |
| 変更履歴 | データ変更の追跡 |

### 接続情報

| 環境 | URL | ポート |
|------|-----|--------|
| 開発（PostgreSQL） | `jdbc:postgresql://localhost:5433/fas` | 5433 |
| デモ（H2） | `jdbc:h2:mem:fas_demo` | - |

## デプロイ

### Heroku へのデプロイ

```bash
# 初回：Heroku アプリ作成
gulp heroku:fas:create

# デプロイ
gulp heroku:fas:deploy
```

### GitHub Actions

- **CI**: `apps/fas/**` の変更で自動実行
- **デプロイ**: main ブランチへの push で自動デプロイ

## Gulp タスク一覧

```bash
# Docker
gulp docker:fas:up        # PostgreSQL 起動
gulp docker:fas:down      # PostgreSQL 停止
gulp docker:fas:clean     # PostgreSQL 停止 + ボリューム削除
gulp docker:fas:status    # コンテナ状態確認
gulp docker:fas:logs      # ログ表示

# SchemaSpy
gulp schemaspy:fas:generate  # ER 図生成
gulp schemaspy:fas:open      # ER 図をブラウザで開く
gulp schemaspy:fas           # 生成して開く

# Heroku
gulp heroku:fas:create    # アプリ作成
gulp heroku:fas:deploy    # デプロイ
gulp heroku:fas:logs      # ログ表示
gulp heroku:fas:open      # ブラウザで開く

# テスト
gulp test:fas:backend     # Backend テスト実行
```

## ライセンス

ISC
