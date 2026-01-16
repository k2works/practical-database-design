# 実践データベース設計：基幹業務システム編

## 概要

基幹業務システム（販売管理・財務会計・生産管理）のデータベース設計を、業務フローとデータモデルの観点から体系的に解説する技術記事プロジェクトです。

### 対象読者

- データベース設計の基礎を学びたいエンジニア
- 基幹業務システムの全体像を理解したい開発者
- 販売管理・財務会計・生産管理の業務知識を習得したい方

### 技術スタック

| カテゴリ | 技術 |
|---------|------|
| 言語 | Java 25 |
| フレームワーク | Spring Boot 4.0.0 |
| ORM | MyBatis 4.0.0 |
| データベース | PostgreSQL 16 |
| マイグレーション | Flyway |
| テスト | JUnit 5 + TestContainers |
| ドキュメント | MkDocs + PlantUML |

### 執筆方針

- ダイアグラムには PlantUML を使用
- 業務フローと ER 図を中心に解説
- 日本語テーブル・日本語カラムでデータベースを定義
- 実装コードは `<details>` タグで表示切替可能

## 記事構成

### 第1部：基幹業務システムの全体像

| 章 | タイトル | 状態 |
|----|----------|------|
| 第1章 | 基幹業務システムとは | 完了 |
| 第2章 | 基幹業務システムの業務領域 | 完了 |
| 第3章 | 業務フローの全体像 | 完了 |

### 第2部：販売管理システム

| 章 | タイトル | 状態 |
|----|----------|----|
| 第4章 | 販売管理システムの全体像 | 完了 |
| 第5章 | マスタ情報の設計 | 完了 |
| 第6章 | 受注・出荷・売上の設計 | 完了 |
| 第7章 | 債権管理の設計 | 完了 |
| 第8章 | 調達管理の設計 | 完了 |
| 第9章 | 在庫管理の設計 | 完了 |
| 第10章 | 債務管理の設計 | 完了 |
| 第11章 | 共通処理の設計 | 完了 |
| 第12章 | 販売管理データ設計（B 社事例） | 完了 |
| 第13章 | API サービスの実装 | 完了 |

### 第3部：財務会計システム

| 章 | タイトル | 状態 |
|----|----------|------|
| 第14章 | 財務会計システムの全体像 | 完了 |
| 第15章 | 勘定科目の設計 | 完了 |
| 第16章 | 仕訳の設計 | 完了 |
| 第17章 | 自動仕訳の設計 | 完了 |
| 第18章 | 勘定科目残高の設計 | 完了 |
| 第19章 | 赤黒とログの設計 | 完了 |
| 第20章 | 財務会計データ設計（D 社事例） | 完了 |
| 第21章 | API サービスの実装 | 完了 |

### 第4部：生産管理システム

| 章 | タイトル | 状態 |
|----|----------|------|
| 第22章 | 生産管理システムの全体像 | 完了 |
| 第23章 | 生産管理のマスタ情報（モノ） | 未着手 |
| 第24章 | 生産管理のマスタ情報（時・人・場所） | 未着手 |
| 第25章 | 生産計画の設計 | 未着手 |
| 第26章 | 購買管理の設計（発注・入荷・検収） | 未着手 |
| 第27章 | 購買管理の設計（外注委託） | 未着手 |
| 第28章 | 工程管理の設計（製造指示・製造実績） | 未着手 |
| 第29章 | 在庫管理の設計（受払・在庫状態） | 未着手 |
| 第30章 | 品質管理の設計 | 未着手 |
| 第31章 | 製造原価管理の設計 | 未着手 |
| 第32章 | 生産管理データ設計（E 社事例） | 未着手 |
| 第33章 | API サービスの実装 | 未着手 |

### 第5部：エンタープライズインテグレーション

| 章 | タイトル | 状態 |
|----|----------|------|
| 第34章 | システム統合の概要 | 未着手 |
| 第35章 | メッセージングパターン | 未着手 |
| 第36章 | システム間連携パターン | 未着手 |
| 第37章 | マスタデータ管理（MDM） | 未着手 |
| 第38章 | イベント駆動アーキテクチャ | 未着手 |
| 第39章 | API 設計とサービス連携 | 未着手 |
| 第40章 | データ連携の実装パターン | 未着手 |

### 付録

| 付録 | タイトル | 状態 |
|------|----------|------|
| A | 全体 ER 図 | 未着手 |
| B | テーブル定義一覧 | 未着手 |
| C | 用語集 | 未着手 |

## 構成

- [構築](#構築)
- [配置](#配置)
- [運用](#運用)
- [開発](#開発)

## デモ

- [販売管理システム（SMS）](https://deploy-demo-sms-b33828d678a9.herokuapp.com/)
- [財務会計システム（FAS）](https://deploy-demo-fas.herokuapp.com/)
- [生産管理システム（PMS）](https://deploy-demo-pms-40869571939f.herokuapp.com/)

## 詳細

### Quick Start

```bash
npm install
npm start
```

以下が自動実行されます：
1. MkDocs サーバー起動 & ブラウザで開く
2. SMS・FAS・PMS PostgreSQL コンテナ起動（並列）
3. SMS・FAS・PMS SchemaSpy ER 図生成（並列）

ブラウザで http://localhost:8000 にアクセスして記事をプレビューできます。

### 構築

#### MCP サーバーの設定

```bash
claude mcp add github npx @modelcontextprotocol/server-github -e GITHUB_PERSONAL_ACCESS_TOKEN=xxxxxxxxxxxxxxx
claude mcp add --transport http byterover-mcp --scope user https://mcp.byterover.dev/v2/mcp
claude mcp add github npx -y @modelcontextprotocol/server-github -s project
```

**[⬆ back to top](#構成)**

### 配置

#### GitHub Pages セットアップ

1. **GitHub リポジトリの Settings を開く**
    - リポジトリページで `Settings` タブをクリック

2. **Pages 設定を開く**
    - 左サイドバーの `Pages` をクリック

3. **Source を設定**
    - `Source` で `Deploy from a branch` を選択
    - `Branch` で `gh-pages` を選択し、フォルダは `/ (root)` を選択
    - `Save` をクリック

4. **初回デプロイ**
    - main ブランチにプッシュすると GitHub Actions が自動実行
    - Actions タブでデプロイ状況を確認

**[⬆ back to top](#構成)**

### 運用

#### ドキュメントの編集

1. ローカル環境で MkDocs サーバーを起動
   ```bash
   docker-compose up mkdocs
   ```
   または、Gulp タスクを使用:
   ```bash
   npm run docs:serve
   ```

2. ブラウザで http://localhost:8000 にアクセスして編集結果をプレビュー

3. `docs/article/` ディレクトリ内の Markdown ファイルを編集

4. 変更をコミットしてプッシュ
   ```bash
   git add .
   git commit -m "ドキュメントの更新"
   git push
   ```

#### Gulp タスクの使用

##### MkDocs タスク

| コマンド | 説明 |
|---------|------|
| `npm run docs:serve` | MkDocs サーバーの起動 |
| `npm run docs:stop` | MkDocs サーバーの停止 |
| `npm run docs:build` | Docker 起動 + ER 図生成 + MkDocs ビルド |

##### SMS Docker タスク

| コマンド | 説明 |
|---------|------|
| `npm run docker:sms:up` | PostgreSQL コンテナ起動 |
| `npm run docker:sms:down` | PostgreSQL コンテナ停止 |
| `gulp docker:sms:build` | 全イメージビルド（backend, schemaspy） |
| `gulp docker:sms:clean` | コンテナ停止 & ボリューム削除（データリセット） |
| `gulp docker:sms:status` | コンテナ状態確認 |
| `gulp docker:sms:logs` | PostgreSQL ログ表示 |
| `gulp docker:sms:restart` | コンテナ再起動 |

##### FAS Docker タスク

| コマンド | 説明 |
|---------|------|
| `npm run docker:fas:up` | PostgreSQL コンテナ起動 |
| `npm run docker:fas:down` | PostgreSQL コンテナ停止 |
| `gulp docker:fas:build` | 全イメージビルド（backend, schemaspy） |
| `gulp docker:fas:clean` | コンテナ停止 & ボリューム削除（データリセット） |
| `gulp docker:fas:status` | コンテナ状態確認 |
| `gulp docker:fas:logs` | PostgreSQL ログ表示 |
| `gulp docker:fas:restart` | コンテナ再起動 |

##### PMS Docker タスク

| コマンド | 説明 |
|---------|------|
| `npm run docker:pms:up` | PostgreSQL コンテナ起動 |
| `npm run docker:pms:down` | PostgreSQL コンテナ停止 |
| `gulp docker:pms:build` | 全イメージビルド（backend, schemaspy） |
| `gulp docker:pms:clean` | コンテナ停止 & ボリューム削除（データリセット） |
| `gulp docker:pms:status` | コンテナ状態確認 |
| `gulp docker:pms:logs` | PostgreSQL ログ表示 |
| `gulp docker:pms:restart` | コンテナ再起動 |

##### SMS SchemaSpy タスク

| コマンド | 説明 |
|---------|------|
| `npm run schemaspy:sms` | SchemaSpy ER 図生成 |
| `gulp schemaspy:sms:open` | 生成済み ER 図をブラウザで開く |
| `gulp schemaspy:sms:clean` | ER 図出力ディレクトリをクリーン |

##### FAS SchemaSpy タスク

| コマンド | 説明 |
|---------|------|
| `npm run schemaspy:fas` | SchemaSpy ER 図生成 |
| `gulp schemaspy:fas:open` | 生成済み ER 図をブラウザで開く |
| `gulp schemaspy:fas:clean` | ER 図出力ディレクトリをクリーン |

##### PMS SchemaSpy タスク

| コマンド | 説明 |
|---------|------|
| `npm run schemaspy:pms` | SchemaSpy ER 図生成 |
| `gulp schemaspy:pms:open` | 生成済み ER 図をブラウザで開く |
| `gulp schemaspy:pms:clean` | ER 図出力ディレクトリをクリーン |

##### SMS Heroku デプロイタスク

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

##### FAS Heroku デプロイタスク

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

##### PMS Heroku デプロイタスク

| コマンド | 説明 |
|---------|------|
| `gulp heroku:pms:create` | Heroku アプリ新規作成 |
| `gulp heroku:pms:deploy` | ビルド → プッシュ → リリース（一括） |
| `gulp heroku:pms:build` | Docker イメージビルド |
| `gulp heroku:pms:push` | イメージを Heroku にプッシュ |
| `gulp heroku:pms:release` | リリース実行 |
| `gulp heroku:pms:logs` | Heroku ログ表示 |
| `gulp heroku:pms:open` | ブラウザでアプリを開く |
| `gulp heroku:pms:restart` | Dyno 再起動（データリセット） |

##### 作業履歴（ジャーナル）タスク

| コマンド | 説明 |
|---------|------|
| `npm run journal` | すべてのコミット日付の作業履歴を生成 |
| `npx gulp journal:generate:date --date=YYYY-MM-DD` | 特定の日付の作業履歴を生成 |

生成された作業履歴は `docs/journal/` ディレクトリに保存されます。

#### GitHub Container Registry

このプロジェクトでは、GitHub Container Registry（GHCR）を使用して開発コンテナイメージを管理しています。

##### 自動ビルド・プッシュ

タグをプッシュすると、GitHub Actions が自動的にコンテナイメージをビルドし、GHCR にプッシュします。

```bash
# タグを作成してプッシュ
git tag 0.0.1
git push origin 0.0.1
```

##### イメージの取得・実行

```bash
# イメージをプル
docker pull ghcr.io/k2works/practical-database-design:latest

# コンテナを実行
docker run -it -v $(pwd):/srv ghcr.io/k2works/practical-database-design:latest
```

##### Dev Container の使用

VS Code で Dev Container を使用する場合：

1. VS Code で「Dev Containers: Reopen in Container」を実行
2. または「Dev Containers: Rebuild and Reopen in Container」で再ビルド

**[⬆ back to top](#構成)**

### 開発

#### ディレクトリ構成

```
practical-database-design/
├── docs/
│   ├── article/           # 記事本体
│   │   ├── index.md       # 記事トップページ
│   │   ├── outline.md     # アウトライン
│   │   ├── workflow.md    # 執筆ワークフロー
│   │   ├── part1/         # 第1部
│   │   ├── part2/         # 第2部
│   │   ├── part3/         # 第3部
│   │   ├── part4/         # 第4部
│   │   ├── part5/         # 第5部
│   │   └── appendix/      # 付録
│   ├── reference/         # 参照ドキュメント
│   └── wiki/              # 参照元記事（wiki サブモジュール）
├── apps/                  # アプリケーション実装
│   ├── sms/               # 販売管理システム（Spring Boot）
│   │   ├── backend/       # バックエンド API
│   │   └── docker-compose.yml
│   ├── fas/               # 財務会計システム（Spring Boot）
│   │   ├── backend/       # バックエンド API
│   │   └── docker-compose.yml
│   └── pms/               # 生産管理システム（Spring Boot）
│       ├── backend/       # バックエンド API
│       └── docker-compose.yml
├── ops/                   # 運用スクリプト
│   └── scripts/           # Gulp タスク定義
├── mkdocs.yml             # MkDocs 設定
├── docker-compose.yml     # Docker 設定
├── gulpfile.js            # Gulp 設定
└── package.json           # npm スクリプト
```

#### 執筆ワークフロー

1. `outline.md` から次の章を選択
2. `docs/wiki/` から参照記事を確認
3. `docs/article/partN/chapterNN.md` を執筆
4. `mkdocs.yml` と `index.md` にエントリを追加
5. ローカルプレビューで確認

詳細は [執筆ワークフロー](docs/article/workflow.md) を参照してください。

#### Nix による開発環境

Nix を使用して、再現可能な開発環境を構築できます。

##### 準備

1. [Nix をインストール](https://nixos.org/download.html)します。
2. Flakes を有効にします（`~/.config/nix/nix.conf` に `experimental-features = nix-command flakes` を追加）。

##### 環境の利用

- **デフォルト環境（共通ツール）に入る:**
  ```bash
  nix develop
  ```

- **Node.js 環境に入る:**
  ```bash
  nix develop .#node
  ```

- **Python/MkDocs 環境に入る:**
  ```bash
  nix develop .#python
  ```

- **SMS 環境に入る:**
  ```bash
  nix develop .#sms
  ```

- **FAS 環境に入る:**
  ```bash
  nix develop .#fas
  ```

- **PMS 環境に入る:**
  ```bash
  nix develop .#pms
  ```

環境から抜けるには `exit` を入力します。

##### 依存関係の更新

```bash
nix flake update
```

**[⬆ back to top](#構成)**

## 参照

- [MkDocs](https://www.mkdocs.org/)
- [PlantUML](https://plantuml.com/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis](https://mybatis.org/mybatis-3/)
