# 運用ガイド

本ドキュメントでは、販売管理システム（SMS）と財務会計システム（FAS）の運用に必要なコマンドと手順を説明します。

---

## Quick Start

```bash
npm install
npm start
```

以下が自動実行されます：
1. MkDocs サーバー起動 & ブラウザで開く
2. SMS・FAS PostgreSQL コンテナ起動（並列）
3. SMS・FAS SchemaSpy ER 図生成（並列）

ブラウザで http://localhost:8000 にアクセスして記事をプレビューできます。

---

## 環境情報

### システム別ポート構成

| システム | アプリケーション | PostgreSQL | データベース名 |
|----------|------------------|------------|----------------|
| SMS（販売管理） | 8080 | 5432 | sms |
| FAS（財務会計） | 8081 | 5433 | fas |

### 接続情報

| システム | JDBC URL |
|----------|----------|
| SMS | `postgresql://postgres:postgres@localhost:5432/sms` |
| FAS | `postgresql://postgres:postgres@localhost:5433/fas` |

---

## Docker タスク

### SMS Docker タスク

| コマンド | 説明 |
|---------|------|
| `npm run docker:sms:up` | PostgreSQL コンテナ起動 |
| `npm run docker:sms:down` | PostgreSQL コンテナ停止 |
| `gulp docker:sms:build` | 全イメージビルド（backend, schemaspy） |
| `gulp docker:sms:clean` | コンテナ停止 & ボリューム削除（データリセット） |
| `gulp docker:sms:status` | コンテナ状態確認 |
| `gulp docker:sms:logs` | PostgreSQL ログ表示 |
| `gulp docker:sms:restart` | コンテナ再起動 |
| `gulp docker:sms:help` | ヘルプ表示 |

### FAS Docker タスク

| コマンド | 説明 |
|---------|------|
| `npm run docker:fas:up` | PostgreSQL コンテナ起動 |
| `npm run docker:fas:down` | PostgreSQL コンテナ停止 |
| `gulp docker:fas:build` | 全イメージビルド（backend, schemaspy） |
| `gulp docker:fas:clean` | コンテナ停止 & ボリューム削除（データリセット） |
| `gulp docker:fas:status` | コンテナ状態確認 |
| `gulp docker:fas:logs` | PostgreSQL ログ表示 |
| `gulp docker:fas:restart` | コンテナ再起動 |
| `gulp docker:fas:help` | ヘルプ表示 |

---

## SchemaSpy タスク

### SMS SchemaSpy タスク

| コマンド | 説明 |
|---------|------|
| `npm run schemaspy:sms` | SchemaSpy ER 図生成 |
| `gulp schemaspy:sms:open` | 生成済み ER 図をブラウザで開く |
| `gulp schemaspy:sms:clean` | ER 図出力ディレクトリをクリーン |
| `gulp schemaspy:sms:regenerate` | クリーン後に ER 図再生成 |
| `gulp schemaspy:sms:help` | ヘルプ表示 |

### FAS SchemaSpy タスク

| コマンド | 説明 |
|---------|------|
| `npm run schemaspy:fas` | SchemaSpy ER 図生成 |
| `gulp schemaspy:fas:open` | 生成済み ER 図をブラウザで開く |
| `gulp schemaspy:fas:clean` | ER 図出力ディレクトリをクリーン |
| `gulp schemaspy:fas:regenerate` | クリーン後に ER 図再生成 |
| `gulp schemaspy:fas:help` | ヘルプ表示 |

### 出力ディレクトリ

| システム | 出力先 |
|----------|--------|
| SMS | `docs/assets/schemaspy-output/sms/` |
| FAS | `docs/assets/schemaspy-output/fas/` |

---

## Heroku デプロイタスク

### SMS Heroku デプロイタスク

| コマンド | 説明 |
|---------|------|
| `gulp heroku:sms:create` | Heroku アプリ新規作成 |
| `gulp heroku:sms:deploy` | ビルド → プッシュ → リリース（一括） |
| `gulp heroku:sms:build` | Docker イメージビルド |
| `gulp heroku:sms:push` | イメージを Heroku にプッシュ |
| `gulp heroku:sms:release` | リリース実行 |
| `gulp heroku:sms:logs` | Heroku ログ表示 |
| `gulp heroku:sms:open` | ブラウザでアプリを開く |
| `gulp heroku:sms:restart` | Dyno 再起動（データリセット） |
| `gulp heroku:sms:info` | アプリ情報表示 |
| `gulp heroku:sms:help` | ヘルプ表示 |

### FAS Heroku デプロイタスク

| コマンド | 説明 |
|---------|------|
| `gulp heroku:fas:create` | Heroku アプリ新規作成 |
| `gulp heroku:fas:deploy` | ビルド → プッシュ → リリース（一括） |
| `gulp heroku:fas:build` | Docker イメージビルド |
| `gulp heroku:fas:push` | イメージを Heroku にプッシュ |
| `gulp heroku:fas:release` | リリース実行 |
| `gulp heroku:fas:logs` | Heroku ログ表示 |
| `gulp heroku:fas:open` | ブラウザでアプリを開く |
| `gulp heroku:fas:restart` | Dyno 再起動（データリセット） |
| `gulp heroku:fas:info` | アプリ情報表示 |
| `gulp heroku:fas:help` | ヘルプ表示 |

### Heroku デモ環境 URL

| システム | URL |
|----------|-----|
| SMS | https://deploy-demo-sms.herokuapp.com/ |
| FAS | https://deploy-demo-fas.herokuapp.com/ |

### Heroku 初回セットアップ手順

```bash
# 1. Heroku CLI でログイン
heroku login

# 2. SMS アプリ作成
gulp heroku:sms:create

# 3. FAS アプリ作成
gulp heroku:fas:create

# 4. デプロイ
gulp heroku:sms:deploy
gulp heroku:fas:deploy
```

### GitHub Actions 自動デプロイ

main ブランチへのプッシュで自動デプロイされます。

**必要な GitHub Secrets:**

| シークレット名 | 内容 |
|----------------|------|
| `HEROKU_API_KEY_SMS` | SMS 用 Heroku API キー |
| `HEROKU_API_KEY_FAS` | FAS 用 Heroku API キー |

---

## CI/CD ワークフロー

### SMS CI ワークフロー

`apps/sms/**` への変更で自動実行されます。

| ジョブ | 内容 |
|--------|------|
| `backend-check` | Gradle fullCheck（テスト + 品質チェック） |

**成果物:**
- `sms-backend-test-report` - テストレポート
- `sms-backend-jacoco-report` - カバレッジレポート

### FAS CI ワークフロー

`apps/fas/**` への変更で自動実行されます。

| ジョブ | 内容 |
|--------|------|
| `backend-check` | Gradle fullCheck（テスト + 品質チェック） |

**成果物:**
- `fas-backend-test-report` - テストレポート
- `fas-backend-jacoco-report` - カバレッジレポート

### ワークフロー一覧

| ワークフロー | トリガー | 説明 |
|-------------|----------|------|
| `ci-sms.yml` | SMS ファイル変更 | SMS バックエンドのテスト・品質チェック |
| `ci-fas.yml` | FAS ファイル変更 | FAS バックエンドのテスト・品質チェック |
| `deploy-demo-sms.yml` | main ブランチへのプッシュ | SMS Heroku デモデプロイ |
| `deploy-demo-fas.yml` | main ブランチへのプッシュ | FAS Heroku デモデプロイ |
| `mkdocs.yml` | main ブランチへのプッシュ | MkDocs GitHub Pages デプロイ |
| `docker-publish.yml` | タグプッシュ | Docker イメージを GHCR に公開 |

---

## MkDocs タスク

| コマンド | 説明 |
|---------|------|
| `npm run docs:serve` | MkDocs サーバーの起動 |
| `npm run docs:stop` | MkDocs サーバーの停止 |
| `npm run docs:build` | Docker 起動 + ER 図生成 + MkDocs ビルド |

---

## 作業履歴（ジャーナル）タスク

| コマンド | 説明 |
|---------|------|
| `npm run journal` | すべてのコミット日付の作業履歴を生成 |
| `npx gulp journal:generate:date --date=YYYY-MM-DD` | 特定の日付の作業履歴を生成 |

生成された作業履歴は `docs/journal/` ディレクトリに保存されます。

---

## ローカル開発

### SMS バックエンド起動

```bash
# PostgreSQL 起動
npm run docker:sms:up

# バックエンド起動（開発モード）
cd apps/sms/backend
./gradlew bootRun

# デモモードで起動（H2 インメモリ）
./gradlew bootRun --args='--spring.profiles.active=demo'
```

### FAS バックエンド起動

```bash
# PostgreSQL 起動
npm run docker:fas:up

# バックエンド起動（開発モード）
cd apps/fas/backend
./gradlew bootRun

# デモモードで起動（H2 インメモリ）
./gradlew bootRun --args='--spring.profiles.active=demo'
```

### 動作確認 URL

| システム | Web アプリ | Swagger UI | H2 コンソール（デモのみ） |
|----------|------------|------------|--------------------------|
| SMS | http://localhost:8080 | http://localhost:8080/swagger-ui.html | http://localhost:8080/h2-console |
| FAS | http://localhost:8081 | http://localhost:8081/swagger-ui.html | http://localhost:8081/h2-console |

---

## トラブルシューティング

### Docker コンテナが起動しない

```bash
# コンテナ状態を確認
gulp docker:sms:status
gulp docker:fas:status

# ログを確認
gulp docker:sms:logs
gulp docker:fas:logs

# クリーンアップして再起動
gulp docker:sms:clean && gulp docker:sms:up
gulp docker:fas:clean && gulp docker:fas:up
```

### ポートが使用中

```bash
# Windows でポートを使用しているプロセスを確認
netstat -ano | findstr :8080
netstat -ano | findstr :8081
netstat -ano | findstr :5432
netstat -ano | findstr :5433

# プロセスを終了
taskkill //F //PID <プロセスID>
```

### Heroku デプロイに失敗

```bash
# Heroku CLI にログイン
heroku login
heroku container:login

# ログを確認
gulp heroku:sms:logs
gulp heroku:fas:logs

# 手動で認証
docker login registry.heroku.com -u _ -p $(heroku auth:token)
```

---

## 関連ドキュメント

- [技術スタック](techstack.md) - 使用技術の詳細
- [執筆ワークフロー](workflow.md) - 開発ワークフロー
- [SMS デプロイ手順書](deploy-sms.md) - SMS デモデプロイの詳細
- [FAS デプロイ手順書](deploy-fas.md) - FAS デモデプロイの詳細
