# 執筆ワークフロー

## 概要

本記事は `outline.md` に定義された構成に従い、章ごとに執筆と実装を同期しながら進める。

## ワークフロー図

```plantuml
@startuml
title 執筆ワークフロー

start

:章の選択;
note right
  outline.md から
  次の章を選択
end note

:参照記事の確認;
note right
  docs/wiki/記事/データベース/
  実践データベース設計/
  から該当記事を参照
end note

:執筆;
note right
  指定された言語で
  章の内容を執筆
end note

:レビュー;

if (修正が必要?) then (yes)
  :修正;
  -> 執筆;
else (no)
endif

:実装;
note right
  執筆内容に基づき
  コードを実装
end note

:同期確認;
note right
  執筆内容と実装の
  整合性を確認
end note

if (不整合あり?) then (yes)
  :執筆内容を更新;
  -> 同期確認;
else (no)
endif

:章の完了;

:mkdocs.yml 更新;
note right
  ナビゲーションに
  章を追加
end note

:ローカルプレビュー確認;

if (次の章あり?) then (yes)
  -> 章の選択;
else (no)
  :記事完成;
  stop
endif

@enduml
```

## 詳細フロー

```plantuml
@startuml
title 章ごとの執筆・実装サイクル

|執筆|
start
:outline.md から章を選択;
:参照記事を特定;

|参照|
:参照記事を読み込み;
note right
  docs/wiki/記事/データベース/
  実践データベース設計/
  - 概要.md
  - 販売管理_2/
  - 財務会計_2/
  - 生産管理/
end note

|執筆|
:章の構成を確認;
:本文を執筆;
:PlantUML ダイアグラム作成;
:ER 図作成;

|レビュー|
:内容レビュー;
:技術的正確性の確認;

|実装|
:データベーススキーマ実装;
:サンプルデータ作成;
:動作確認;

|同期|
:執筆内容と実装の照合;

if (差異あり?) then (yes)
  :執筆内容を修正;
  :実装を修正;
else (no)
endif

:章の完了をマーク;

|公開|
:mkdocs.yml に章を追加;
:mkdocs serve でプレビュー;

if (表示問題あり?) then (yes)
  :問題を修正;
else (no)
endif

:index.md のリンクを有効化;

|執筆|
if (全章完了?) then (no)
  -> outline.md から章を選択;
else (yes)
  :最終レビュー;
  :mkdocs build;
  :記事公開;
  stop
endif

@enduml
```

## MkDocs 反映ワークフロー

```plantuml
@startuml
title MkDocs 反映フロー

start

:章ファイル作成;
note right
  docs/article/partN/chapterNN.md
end note

:mkdocs.yml 更新;
note right
  nav セクションに
  新しい章を追加
end note

:index.md 更新;
note right
  目次リンクを
  有効化
end note

:ローカルプレビュー;
note right
  mkdocs serve
  http://localhost:8000
end note

if (問題あり?) then (yes)
  :修正;
  -> ローカルプレビュー;
else (no)
endif

:コミット;

if (デプロイ?) then (yes)
  :mkdocs build;
  :デプロイ実行;
else (no)
endif

stop

@enduml
```

### MkDocs 更新手順

#### 1. mkdocs.yml への章追加

```yaml
nav:
  - 実践データベース設計:
      - 第N部 セクション名:
          - 第N章 章タイトル: article/partN/chapterNN.md
```

#### 2. ローカルプレビュー

```bash
# サーバー起動
mkdocs serve

# ブラウザで確認
# http://localhost:8000
```

#### 3. ビルド・デプロイ

```bash
# 静的サイト生成
mkdocs build

# GitHub Pages へデプロイ（設定済みの場合）
mkdocs gh-deploy
```

### MkDocs チェックリスト

- [ ] 章ファイルが正しいパスに配置されている
- [ ] mkdocs.yml の nav に章が追加されている
- [ ] index.md のリンクが正しい
- [ ] ローカルプレビューで表示確認済み
- [ ] PlantUML ダイアグラムが正しくレンダリングされる
- [ ] 内部リンクが正常に動作する

## 執筆ルール

### 1. 章の選択

- `outline.md` の順序に従って進める
- 依存関係がある場合は先行章を優先

### 2. 参照記事

| 部 | 参照先 |
|---|---|
| 第1部：基幹業務システムの全体像 | `概要.md` |
| 第2部：販売管理システム | `販売管理_2/` |
| 第3部：財務会計システム | `財務会計_2/` |
| 第4部：生産管理システム | `生産管理/` |
| 第5部：エンタープライズインテグレーション | 各部の連携仕様 |

### 3. 執筆フォーマット

```markdown
# 第N章：章タイトル

## N.1 セクションタイトル

本文...

### ダイアグラム

\```plantuml
@startuml
...
@enduml
\```

### ER 図

\```plantuml
@startuml
entity "テーブル名" {
  ...
}
@enduml
\```

### 実装

<details>
<summary>SQL 実装</summary>

\```sql
CREATE TABLE ...
\```

</details>
```
- タスク項目などは一行開けて記述する
- NG
  ```markdown
    **受入条件**:
    - [ ] ログアウトボタンをクリックするとログアウトできる
    - [ ] ログアウト後、ログイン画面に遷移する
    - [ ] JWT トークンが無効化される
  ```
    - OK
  ```markdown
    **受入条件**:
  
    - [ ] ログアウトボタンをクリックするとログアウトできる
    - [ ] ログアウト後、ログイン画面に遷移する
    - [ ] JWT トークンが無効化される
  ```
  
### 4. 実装同期チェックリスト

- [ ] テーブル定義が執筆内容と一致
- [ ] カラム名・型が一致
- [ ] リレーションが一致
- [ ] サンプルデータで動作確認済み

## ファイル構成

```
docs/article/
├── index.md            # 記事トップページ（目次）
├── outline.md          # 全体構成
├── workflow.md         # 本ファイル（執筆ワークフロー）
├── part1/              # 第1部
│   ├── chapter01.md
│   ├── chapter02.md
│   └── chapter03.md
├── part2/              # 第2部
│   ├── chapter04.md
│   └── ...
├── part3/              # 第3部
├── part4/              # 第4部
├── part5/              # 第5部
└── appendix/           # 付録
    ├── er-diagrams.md
    ├── table-definitions.md
    └── glossary.md
```

## 進捗管理

| 章 | ステータス | 執筆日 | 実装日 | 同期確認日 |
|---|---|---|---|---|
| 第1章 | 未着手 | - | - | - |
| 第2章 | 未着手 | - | - | - |
| ... | ... | ... | ... | ... |
