# 第17章：自動仕訳の設計

販売管理システムなどの業務システムから会計システムへの自動仕訳処理を TDD で設計していきます。売上データから仕訳データへの自動変換ルールと、効率的なバッチ処理の設計を行います。

---

## 17.1 自動仕訳の概要

### 販売管理システムと会計システムの連携

自動仕訳は、業務システムのトランザクションデータを会計システムの仕訳データへ自動的に変換する機能です。

#### 従来のアナログ連携（手入力による非連携）

従来の方式では、販売管理システムで発行した売上伝票を紙で経理部門に回送し、経理担当者が仕訳入力を行っていました。

```plantuml
@startuml
title 販売管理システムと会計システムの非連携（アナログ連携）

skinparam rectangle {
    BackgroundColor White
    BorderColor Black
}
skinparam database {
    BackgroundColor #E8F5E9
    BorderColor Black
}

package "営業部門" {
    rectangle "売上入力" as SalesInput <<Screen>>
    database "売上データ" as SalesDB
    rectangle "売上伝票" as SalesVoucher <<Document>>
}

package "経理部門" {
    rectangle "仕訳入力" as JournalInput <<Screen>>
    database "仕訳データ" as JournalDB
    rectangle "損益計算書\n貸借対照表" as FinancialReports <<Document>>
}

SalesInput -down-> SalesDB
SalesDB -down-> SalesVoucher

SalesVoucher -[#red,bold]right-> JournalInput : **紙伝票の回送**\n**(手入力による再登録)**

JournalInput -down-> JournalDB
JournalDB -down-> FinancialReports

@enduml
```

#### アナログ連携の問題点

| 問題 | 説明 |
|-----|------|
| **二重入力** | 営業部門と経理部門で同じ情報を入力する作業負荷 |
| **入力ミス** | 手入力によるデータ不整合 |
| **タイムラグ** | 紙伝票の回送による情報遅延 |
| **消費税計算ミス** | 手計算による誤り |
| **勘定科目選択ミス** | 担当者の判断ばらつき |

### 自動仕訳処理のデータフロー

自動仕訳処理を導入することで、売上データから仕訳データへの変換を自動化します。

```plantuml
@startuml
title 自動仕訳処理のデータの流れ

skinparam rectangle {
    BackgroundColor White
    BorderColor Black
}
skinparam database {
    BackgroundColor #E8F5E9
    BorderColor Black
}

package "営業部門" {
    rectangle "売上入力" as SalesInput <<Screen>>
    database "売上データ" as SalesDB
    rectangle "売上\nチェックリスト" as SalesList <<Document>>
}

package "自動仕訳機能" #F1F8E9 {
    database "自動仕訳\nパターンマスタ" as PatternMaster
    rectangle "自動仕訳処理" as AutoJournalProcess <<Process>> #4CAF50
    database "自動仕訳データ" as AutoJournalDB
    database "エラーデータ" as ErrorDB
    rectangle "エラーリスト" as ErrorList <<Document>>
    rectangle "自動仕訳\nチェックリスト" as AutoJournalList <<Document>>

    rectangle "転記処理" as PostingProcess <<Process>> #4CAF50
    circle " " as Switch
}

package "経理部門" {
    rectangle "仕訳入力" as JournalInput <<Screen>>
    database "仕訳データ" as JournalDB
}

SalesInput -down-> SalesDB
SalesDB -down-> SalesList

SalesDB -right-> AutoJournalProcess
PatternMaster -down-> AutoJournalProcess
AutoJournalProcess -right-> AutoJournalDB
AutoJournalProcess -down-> ErrorDB
ErrorDB -down-> ErrorList
AutoJournalDB -down-> AutoJournalList

AutoJournalDB -right-> Switch
note top of Switch : (転記指示)
Switch -right-> PostingProcess
PostingProcess -right-> JournalDB

JournalInput -down-> JournalDB

@enduml
```

### 自動仕訳処理の流れ

```plantuml
@startuml

title 自動仕訳処理フロー

|営業部門|
start
:売上登録;

|自動仕訳処理|
:売上データ抽出;
note right
  未処理の売上データを
  抽出条件に基づいて取得
end note

:パターンマッチング;
note right
  商品区分・顧客区分から
  適用する仕訳パターンを決定
end note

if (パターン適合？) then (はい)
  :仕訳データ生成;
  :自動仕訳データに保存;
else (いいえ)
  :エラーデータに保存;
  :エラーリスト出力;
  stop
endif

:売上データに処理済フラグ設定;

|経理部門|
:自動仕訳チェックリスト確認;

if (確認OK？) then (はい)
  :転記処理実行;
  :仕訳データに登録;
else (いいえ)
  :修正・再処理;
  stop
endif

:仕訳完了;
stop

@enduml
```

---

## 17.2 自動仕訳パターンマスタの設計

### 商品グループ × 顧客グループによる条件分岐

自動仕訳パターンマスタは、売上データから仕訳データへの変換ルールを定義するマスタテーブルです。

```plantuml
@startuml

title 自動仕訳パターンマスタ

entity "自動仕訳パターンマスタ" as AutoJournalPattern {
  * **パターンコード**: VARCHAR(10) <<PK>>
  --
  * **パターン名**: VARCHAR(50)
  * **商品グループ**: VARCHAR(10)
  * **顧客グループ**: VARCHAR(10)
  * **売上区分**: VARCHAR(2)
  * **借方勘定科目コード**: VARCHAR(5) <<FK>>
  * **借方補助科目設定**: VARCHAR(20)
  * **貸方勘定科目コード**: VARCHAR(5) <<FK>>
  * **貸方補助科目設定**: VARCHAR(20)
  * **返品時借方科目コード**: VARCHAR(5)
  * **返品時貸方科目コード**: VARCHAR(5)
  * **消費税処理区分**: VARCHAR(2)
  * **有効開始日**: DATE
  * **有効終了日**: DATE
  * **優先順位**: INTEGER
  * **作成日時**: TIMESTAMP
  * **更新日時**: TIMESTAMP
}

@enduml
```

### パターンマスタの項目説明

| 項目 | 説明 | 例 |
|-----|------|-----|
| **パターンコード** | パターンを一意に識別するコード | `P001` |
| **パターン名** | パターンの名称 | `加工品売上` |
| **商品グループ** | 対象商品グループ（`ALL`は全て） | `加工品`, `生鮮品`, `ALL` |
| **顧客グループ** | 対象顧客グループ（`ALL`は全て） | `一般`, `特約店`, `ALL` |
| **売上区分** | 売上の種類 | `01`:通常売上, `02`:返品 |
| **借方勘定科目コード** | 借方に計上する勘定科目 | `11300`(売掛金) |
| **貸方勘定科目コード** | 貸方に計上する勘定科目 | `41110`(売上加工品) |
| **優先順位** | パターン適用の優先度（小さい方が優先） | `100` |

### パターンマスタのデータ例

| パターンコード | パターン名 | 商品グループ | 顧客グループ | 借方科目 | 貸方科目 | 優先順位 |
|-------------|----------|------------|------------|---------|---------|---------|
| P001 | 加工品売上（特約店） | 加工品 | 特約店 | 売掛金 | 売上加工品 | 10 |
| P002 | 加工品売上（一般） | 加工品 | ALL | 売掛金 | 売上加工品 | 20 |
| P003 | 生鮮品売上 | 生鮮品 | ALL | 売掛金 | 売上生鮮品 | 30 |
| P999 | その他売上 | ALL | ALL | 売掛金 | 売上その他 | 999 |

### 売上返品時の科目設定

返品時は通常売上と逆の仕訳が必要です。

| 取引 | 借方 | 貸方 |
|-----|-----|-----|
| **通常売上** | 売掛金 | 売上 |
| **売上返品** | 売上 | 売掛金 |

---

## 17.3 自動仕訳処理の設計

### フラグ管理方式と日付管理方式

売上データの処理状態を管理する方式には、2つのアプローチがあります。

#### フラグ管理方式

```plantuml
@startuml

title フラグ管理方式

[*] --> 売上データ
売上データ : 送信済フラグ: char(1)
売上データ -> 自動仕訳 : 抽出 where 送信済フラグ=0
自動仕訳 -> 売上データ : 更新 set 送信済フラグ=1
自動仕訳 -> 仕訳データ
仕訳データ --> [*]

@enduml
```

| メリット | デメリット |
|---------|----------|
| シンプルな実装 | 再処理時にフラグリセットが必要 |
| 処理状態が明確 | 大量データでの更新負荷 |

#### 日付管理方式

```plantuml
@startuml

title 日付管理方式

[*] --> 売上データ
売上データ : 最終更新日
売上データ -> 自動仕訳 : 抽出 where 最終更新日 > 最終処理日
自動仕訳 --> 自動仕訳管理データ : 更新
自動仕訳管理データ : 最終処理日
自動仕訳 -> 仕訳データ
仕訳データ --> [*]

@enduml
```

| メリット | デメリット |
|---------|----------|
| 差分処理が容易 | 管理テーブルが必要 |
| 再処理時の柔軟性 | 更新日時の整合性管理が必要 |

### セット中心のアプリケーション設計

大量データを効率的に処理するには、ループ処理よりもセット中心処理が有効です。

#### ループ処理（非効率）

```plantuml
@startuml

title ループ処理

[*] --> 売上データ: 対象データ :1000件/日
売上データ : (3年分 100万件)
売上データ -> 自動仕訳 : ①対象データを1万件抽出
自動仕訳マスタ --> 自動仕訳
自動仕訳マスタ : 10パターン
自動仕訳 -> 仕訳データ : ②自動仕訳パターンに変換して挿入
仕訳データ --> [*]

@enduml
```

**処理方式：**
1. 売上データを1件ずつ読み込む
2. 各売上に対してパターンマスタを検索
3. 1件ずつ仕訳データを挿入

**問題点：** N × M 回のDB操作が発生（N=売上件数、M=パターン数）

#### セット中心処理（効率的）

```plantuml
@startuml

title セット中心処理

[*] --> 売上データ: 対象データ :1000件/日
売上データ : (3年分 100万件)
売上データ -> 自動仕訳
自動仕訳マスタ --> 自動仕訳 : ①パターンを1件ずつ読む(10件)
自動仕訳マスタ : 10パターン
自動仕訳 -> 仕訳データ : ②パターンに合う売上データを変換して挿入
仕訳データ --> [*]

@enduml
```

**処理方式：**
1. パターンマスタを1件読み込む（10件）
2. 該当パターンの売上データを一括 INSERT
3. パターン数分のDB操作で完了

**利点：** M 回（パターン数）のDB操作で完了

### 自動仕訳関連テーブルのER図

```plantuml
@startuml

entity "自動仕訳パターンマスタ" as AutoJournalPattern {
  * **パターンコード**: VARCHAR(10) <<PK>>
  --
  パターン名
  商品グループ
  顧客グループ
  借方勘定科目コード <<FK>>
  貸方勘定科目コード <<FK>>
  有効開始日
  有効終了日
  優先順位
  ...
}

entity "自動仕訳データ" as AutoJournal {
  * **自動仕訳番号**: VARCHAR(15) <<PK>>
  --
  売上番号 <<FK>>
  売上行番号
  パターンコード <<FK>>
  起票日
  仕訳行貸借区分
  勘定科目コード <<FK>>
  補助科目コード
  部門コード <<FK>>
  仕訳金額
  消費税額
  処理ステータス
  転記済フラグ
  転記日
  仕訳伝票番号
  エラーコード
  エラーメッセージ
  ...
}

entity "自動仕訳処理履歴" as AutoJournalHistory {
  * **処理番号**: VARCHAR(15) <<PK>>
  --
  処理日時
  処理対象開始日
  処理対象終了日
  処理件数
  成功件数
  エラー件数
  処理金額合計
  処理者
  備考
  ...
}

entity "売上データ" as Sales {
  * **売上番号**: VARCHAR(10) <<PK>>
  --
  売上日
  顧客コード
  自動仕訳処理日
  ...
}

entity "勘定科目マスタ" as Account {
  * **勘定科目コード** <<PK>>
  --
  勘定科目名
  ...
}

AutoJournalPattern }o--|| Account : 借方科目
AutoJournalPattern }o--|| Account : 貸方科目
AutoJournal }o--|| AutoJournalPattern
AutoJournal }o--|| Sales
AutoJournal }o--|| Account

@enduml
```

### マイグレーション：自動仕訳テーブルの作成

<details>
<summary>マイグレーションSQL</summary>

```sql
-- src/main/resources/db/migration/V007__create_auto_journal_tables.sql

-- 自動仕訳処理ステータス
CREATE TYPE 自動仕訳ステータス AS ENUM ('処理待ち', '処理中', '処理完了', '転記済', 'エラー');

-- 自動仕訳パターンマスタ
CREATE TABLE "自動仕訳パターンマスタ" (
    "パターンコード" VARCHAR(10) PRIMARY KEY,
    "パターン名" VARCHAR(50) NOT NULL,
    "商品グループ" VARCHAR(10) DEFAULT 'ALL',
    "顧客グループ" VARCHAR(10) DEFAULT 'ALL',
    "売上区分" VARCHAR(2) DEFAULT '01',
    "借方勘定科目コード" VARCHAR(5) NOT NULL,
    "借方補助科目設定" VARCHAR(20),
    "貸方勘定科目コード" VARCHAR(5) NOT NULL,
    "貸方補助科目設定" VARCHAR(20),
    "返品時借方科目コード" VARCHAR(5),
    "返品時貸方科目コード" VARCHAR(5),
    "消費税処理区分" VARCHAR(2) DEFAULT '01',
    "有効開始日" DATE DEFAULT CURRENT_DATE,
    "有効終了日" DATE DEFAULT '9999-12-31',
    "優先順位" INTEGER DEFAULT 100,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_自動仕訳パターン_借方科目"
        FOREIGN KEY ("借方勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード"),
    CONSTRAINT "fk_自動仕訳パターン_貸方科目"
        FOREIGN KEY ("貸方勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード")
);

-- 自動仕訳データ
CREATE TABLE "自動仕訳データ" (
    "自動仕訳番号" VARCHAR(15) PRIMARY KEY,
    "売上番号" VARCHAR(10) NOT NULL,
    "売上行番号" SMALLINT NOT NULL,
    "パターンコード" VARCHAR(10) NOT NULL,
    "起票日" DATE NOT NULL,
    "仕訳行貸借区分" 仕訳行貸借区分 NOT NULL,
    "勘定科目コード" VARCHAR(5) NOT NULL,
    "補助科目コード" VARCHAR(10),
    "部門コード" VARCHAR(5),
    "仕訳金額" DECIMAL(14,2) NOT NULL,
    "消費税額" DECIMAL(14,2) DEFAULT 0,
    "処理ステータス" 自動仕訳ステータス DEFAULT '処理待ち' NOT NULL,
    "転記済フラグ" SMALLINT DEFAULT 0,
    "転記日" DATE,
    "仕訳伝票番号" VARCHAR(10),
    "エラーコード" VARCHAR(10),
    "エラーメッセージ" VARCHAR(200),
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "更新日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT "fk_自動仕訳_パターン"
        FOREIGN KEY ("パターンコード") REFERENCES "自動仕訳パターンマスタ"("パターンコード"),
    CONSTRAINT "fk_自動仕訳_勘定科目"
        FOREIGN KEY ("勘定科目コード") REFERENCES "勘定科目マスタ"("勘定科目コード"),
    CONSTRAINT "fk_自動仕訳_部門"
        FOREIGN KEY ("部門コード") REFERENCES "部門マスタ"("部門コード")
);

-- 自動仕訳処理履歴
CREATE TABLE "自動仕訳処理履歴" (
    "処理番号" VARCHAR(15) PRIMARY KEY,
    "処理日時" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "処理対象開始日" DATE NOT NULL,
    "処理対象終了日" DATE NOT NULL,
    "処理件数" INTEGER DEFAULT 0,
    "成功件数" INTEGER DEFAULT 0,
    "エラー件数" INTEGER DEFAULT 0,
    "処理金額合計" DECIMAL(15,2) DEFAULT 0,
    "処理者" VARCHAR(50),
    "備考" TEXT,
    "作成日時" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- インデックス
CREATE INDEX "idx_自動仕訳パターン_商品グループ" ON "自動仕訳パターンマスタ"("商品グループ");
CREATE INDEX "idx_自動仕訳パターン_顧客グループ" ON "自動仕訳パターンマスタ"("顧客グループ");
CREATE INDEX "idx_自動仕訳パターン_優先順位" ON "自動仕訳パターンマスタ"("優先順位");
CREATE INDEX "idx_自動仕訳_売上番号" ON "自動仕訳データ"("売上番号");
CREATE INDEX "idx_自動仕訳_処理ステータス" ON "自動仕訳データ"("処理ステータス");
CREATE INDEX "idx_自動仕訳_転記済フラグ" ON "自動仕訳データ"("転記済フラグ");
CREATE INDEX "idx_自動仕訳_起票日" ON "自動仕訳データ"("起票日");
```

</details>

---

## 17.4 売上伝票から仕訳伝票への転記ロジック

### 明細行の分解と集約

売上伝票の明細行を、仕訳パターンに基づいて借方・貸方に分解します。

#### 売上伝票の例

| 項目 | 値 |
|-----|-----|
| 伝票日付 | 2024/04/01 |
| 顧客 | DBMフード新宿 |
| 明細1 | いちご蒸缶（加工品）1,000個 × 1,000円 = 1,000,000円 |
| 明細2 | アスパラ（生鮮品）200個 × 10,000円 = 2,000,000円 |
| 明細3 | さざえのエスカルゴ（加工品）1,500個 × 100円 = 150,000円 |
| 合計 | 3,150,000円（税抜）+ 消費税 315,000円 = 3,465,000円（税込） |

#### 仕訳伝票への変換

```plantuml
@startuml

title 売上伝票から仕訳伝票への変換

rectangle "売上伝票" {
  card "いちご蒸缶\n1,000,000円" as s1
  card "アスパラ\n2,000,000円" as s2
  card "さざえのエスカルゴ\n150,000円" as s3
}

rectangle "変換ルール" as rule {
  card "商品区分: 加工品\n→ 売上加工品" as r1
  card "商品区分: 生鮮品\n→ 売上生鮮品" as r2
  card "請求先: 本社に集約\n→ 売掛金/本社" as r3
}

rectangle "仕訳伝票" {
  card "【借方】\n売掛金/DBMフード本社\n3,465,000円" as d1
  card "【貸方】\n売上加工品 1,150,000円\n売上生鮮品 2,000,000円\n仮受消費税 315,000円" as c1
}

s1 --> rule
s2 --> rule
s3 --> rule
rule --> d1
rule --> c1

note bottom of rule
  **変換のポイント**
  ・商品区分による科目振り分け
  ・請求先への売掛金集約
  ・消費税の自動計算
end note

@enduml
```

### 借方（売掛金）と貸方（売上）の展開

| 借方科目 | 借方金額 | 貸方科目 | 貸方金額 | 摘要 |
|---------|---------|---------|---------|-----|
| 売掛金/DBMフード本社 | 3,465,000 | | | 売上計上 |
| | | 売上加工品/DBMフード新宿 | 1,150,000 | いちご蒸缶・さざえ |
| | | 売上生鮮品/DBMフード新宿 | 2,000,000 | アスパラ |
| | | 仮受消費税 | 315,000 | 消費税10% |

### 消費税の按分計算

消費税額は、明細行ごとの税抜金額に比例して按分します。

```
消費税按分計算：
- 加工品合計: 1,150,000円 → 消費税: 115,000円
- 生鮮品合計: 2,000,000円 → 消費税: 200,000円
- 合計消費税: 315,000円
```

### 貸借一致の原則

仕訳データは必ず借方合計と貸方合計が一致する必要があります。

```
借方合計: 3,465,000円
貸方合計: 1,150,000 + 2,000,000 + 315,000 = 3,465,000円
→ 貸借一致 OK
```

### TDD による自動仕訳処理の実装

#### ドメイン層：エンティティ

<details>
<summary>自動仕訳ステータス Enum</summary>

```java
// src/main/java/com/example/accounting/domain/model/autojournal/AutoJournalStatus.java
package com.example.accounting.domain.model.autojournal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AutoJournalStatus {
    PENDING("処理待ち"),
    PROCESSING("処理中"),
    COMPLETED("処理完了"),
    POSTED("転記済"),
    ERROR("エラー");

    private final String displayName;

    public static AutoJournalStatus fromDisplayName(String displayName) {
        for (AutoJournalStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("不正な自動仕訳ステータス: " + displayName);
    }
}
```

</details>

<details>
<summary>自動仕訳パターンマスタエンティティ</summary>

```java
// src/main/java/com/example/accounting/domain/model/autojournal/AutoJournalPattern.java
package com.example.accounting.domain.model.autojournal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoJournalPattern {
    private String patternCode;           // パターンコード
    private String patternName;           // パターン名
    private String productGroup;          // 商品グループ
    private String customerGroup;         // 顧客グループ
    private String salesType;             // 売上区分
    private String debitAccountCode;      // 借方勘定科目コード
    private String debitSubAccountSetting; // 借方補助科目設定
    private String creditAccountCode;     // 貸方勘定科目コード
    private String creditSubAccountSetting; // 貸方補助科目設定
    private String returnDebitAccountCode; // 返品時借方科目コード
    private String returnCreditAccountCode; // 返品時貸方科目コード
    private String taxProcessingType;     // 消費税処理区分
    private LocalDate validFrom;          // 有効開始日
    private LocalDate validTo;            // 有効終了日
    private Integer priority;             // 優先順位
    private LocalDateTime createdAt;      // 作成日時
    private LocalDateTime updatedAt;      // 更新日時

    /**
     * 指定日付に有効かどうかを判定
     */
    public boolean isValidAt(LocalDate date) {
        return !date.isBefore(validFrom) && !date.isAfter(validTo);
    }

    /**
     * 商品グループと顧客グループにマッチするか判定
     */
    public boolean matches(String productGroup, String customerGroup) {
        boolean productMatch = "ALL".equals(this.productGroup) ||
                               this.productGroup.equals(productGroup);
        boolean customerMatch = "ALL".equals(this.customerGroup) ||
                                this.customerGroup.equals(customerGroup);
        return productMatch && customerMatch;
    }
}
```

</details>

<details>
<summary>自動仕訳データエンティティ</summary>

```java
// src/main/java/com/example/accounting/domain/model/autojournal/AutoJournalEntry.java
package com.example.accounting.domain.model.autojournal;

import com.example.accounting.domain.model.account.DebitCreditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoJournalEntry {
    private String autoJournalNumber;     // 自動仕訳番号
    private String salesNumber;           // 売上番号
    private Integer salesLineNumber;      // 売上行番号
    private String patternCode;           // パターンコード
    private LocalDate postingDate;        // 起票日
    private DebitCreditType debitCreditType; // 仕訳行貸借区分
    private String accountCode;           // 勘定科目コード
    private String subAccountCode;        // 補助科目コード
    private String departmentCode;        // 部門コード
    private BigDecimal amount;            // 仕訳金額
    private BigDecimal taxAmount;         // 消費税額
    private AutoJournalStatus status;     // 処理ステータス
    private Boolean postedFlag;           // 転記済フラグ
    private LocalDate postedDate;         // 転記日
    private String journalVoucherNumber;  // 仕訳伝票番号
    private String errorCode;             // エラーコード
    private String errorMessage;          // エラーメッセージ
    private LocalDateTime createdAt;      // 作成日時
    private LocalDateTime updatedAt;      // 更新日時
}
```

</details>

<details>
<summary>自動仕訳処理履歴エンティティ</summary>

```java
// src/main/java/com/example/accounting/domain/model/autojournal/AutoJournalHistory.java
package com.example.accounting.domain.model.autojournal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoJournalHistory {
    private String processNumber;         // 処理番号
    private LocalDateTime processDateTime; // 処理日時
    private LocalDate targetFromDate;     // 処理対象開始日
    private LocalDate targetToDate;       // 処理対象終了日
    private Integer totalCount;           // 処理件数
    private Integer successCount;         // 成功件数
    private Integer errorCount;           // エラー件数
    private BigDecimal totalAmount;       // 処理金額合計
    private String processedBy;           // 処理者
    private String remarks;               // 備考
    private LocalDateTime createdAt;      // 作成日時
}
```

</details>

#### テストコード

<details>
<summary>自動仕訳パターンのテスト</summary>

```java
// src/test/java/com/example/accounting/domain/model/autojournal/AutoJournalPatternTest.java
package com.example.accounting.domain.model.autojournal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("自動仕訳パターン")
class AutoJournalPatternTest {

    @Nested
    @DisplayName("パターンマッチング")
    class PatternMatching {

        @Test
        @DisplayName("商品グループALLは全ての商品グループにマッチする")
        void shouldMatchAllProductGroups() {
            // Arrange: 商品グループALLのパターン
            var pattern = AutoJournalPattern.builder()
                .patternCode("P001")
                .productGroup("ALL")
                .customerGroup("ALL")
                .build();

            // Act & Assert
            assertThat(pattern.matches("加工品", "一般")).isTrue();
            assertThat(pattern.matches("生鮮品", "一般")).isTrue();
            assertThat(pattern.matches("雑貨", "特約店")).isTrue();
        }

        @Test
        @DisplayName("特定の商品グループのみにマッチする")
        void shouldMatchSpecificProductGroup() {
            // Arrange: 加工品専用パターン
            var pattern = AutoJournalPattern.builder()
                .patternCode("P002")
                .productGroup("加工品")
                .customerGroup("ALL")
                .build();

            // Act & Assert
            assertThat(pattern.matches("加工品", "一般")).isTrue();
            assertThat(pattern.matches("生鮮品", "一般")).isFalse();
        }

        @Test
        @DisplayName("商品グループと顧客グループの両方でマッチングする")
        void shouldMatchBothProductAndCustomerGroup() {
            // Arrange: 特定の組み合わせパターン
            var pattern = AutoJournalPattern.builder()
                .patternCode("P003")
                .productGroup("加工品")
                .customerGroup("特約店")
                .build();

            // Act & Assert
            assertThat(pattern.matches("加工品", "特約店")).isTrue();
            assertThat(pattern.matches("加工品", "一般")).isFalse();
            assertThat(pattern.matches("生鮮品", "特約店")).isFalse();
        }
    }

    @Nested
    @DisplayName("有効期間チェック")
    class ValidityCheck {

        @Test
        @DisplayName("有効期間内の日付でtrueを返す")
        void shouldReturnTrueForValidDate() {
            // Arrange
            var pattern = AutoJournalPattern.builder()
                .patternCode("P001")
                .validFrom(LocalDate.of(2024, 1, 1))
                .validTo(LocalDate.of(2024, 12, 31))
                .build();

            // Act & Assert
            assertThat(pattern.isValidAt(LocalDate.of(2024, 6, 15))).isTrue();
            assertThat(pattern.isValidAt(LocalDate.of(2024, 1, 1))).isTrue();
            assertThat(pattern.isValidAt(LocalDate.of(2024, 12, 31))).isTrue();
        }

        @Test
        @DisplayName("有効期間外の日付でfalseを返す")
        void shouldReturnFalseForInvalidDate() {
            // Arrange
            var pattern = AutoJournalPattern.builder()
                .patternCode("P001")
                .validFrom(LocalDate.of(2024, 1, 1))
                .validTo(LocalDate.of(2024, 12, 31))
                .build();

            // Act & Assert
            assertThat(pattern.isValidAt(LocalDate.of(2023, 12, 31))).isFalse();
            assertThat(pattern.isValidAt(LocalDate.of(2025, 1, 1))).isFalse();
        }
    }
}
```

</details>

#### アプリケーション層（Output Port）

<details>
<summary>自動仕訳リポジトリインターフェース</summary>

```java
// src/main/java/com/example/accounting/application/port/out/AutoJournalRepository.java
package com.example.accounting.application.port.out;

import com.example.accounting.domain.model.autojournal.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 自動仕訳リポジトリ（Output Port）
 */
public interface AutoJournalRepository {

    // パターンマスタ操作
    void savePattern(AutoJournalPattern pattern);

    Optional<AutoJournalPattern> findPatternByCode(String patternCode);

    List<AutoJournalPattern> findAllPatterns();

    List<AutoJournalPattern> findValidPatterns(LocalDate date);

    void updatePattern(AutoJournalPattern pattern);

    void deletePattern(String patternCode);

    // 自動仕訳エントリ操作
    void saveEntry(AutoJournalEntry entry);

    Optional<AutoJournalEntry> findEntryByNumber(String autoJournalNumber);

    List<AutoJournalEntry> findEntriesBySalesNumber(String salesNumber);

    List<AutoJournalEntry> findUnpostedEntries();

    List<AutoJournalEntry> findUnpostedEntriesByDate(LocalDate date);

    List<AutoJournalEntry> findEntriesByStatus(AutoJournalStatus status);

    void updateEntry(AutoJournalEntry entry);

    // 処理履歴操作
    void saveHistory(AutoJournalHistory history);

    Optional<AutoJournalHistory> findHistoryByNumber(String processNumber);

    List<AutoJournalHistory> findHistoriesByDateRange(LocalDate fromDate, LocalDate toDate);
}
```

</details>

---

## 第17章のまとめ

### 作成したテーブル

| テーブル名 | 説明 |
|----------|------|
| `自動仕訳パターンマスタ` | 売上データから仕訳データへの変換ルール |
| `自動仕訳データ` | 変換された仕訳データ（転記前の中間データ） |
| `自動仕訳処理履歴` | 自動仕訳処理の実行履歴 |

### ER図

```plantuml
@startuml

title 自動仕訳テーブル ER図

entity 自動仕訳パターンマスタ {
  パターンコード <<PK>>
  --
  パターン名
  商品グループ
  顧客グループ
  借方勘定科目コード <<FK>>
  貸方勘定科目コード <<FK>>
  有効開始日
  有効終了日
  優先順位
  ...
}

entity 自動仕訳データ {
  自動仕訳番号 <<PK>>
  --
  売上番号
  売上行番号
  パターンコード <<FK>>
  起票日
  仕訳行貸借区分
  勘定科目コード <<FK>>
  仕訳金額
  処理ステータス
  転記済フラグ
  仕訳伝票番号
  ...
}

entity 自動仕訳処理履歴 {
  処理番号 <<PK>>
  --
  処理日時
  処理対象開始日
  処理対象終了日
  処理件数
  成功件数
  エラー件数
  処理金額合計
  ...
}

entity 勘定科目マスタ {
  勘定科目コード <<PK>>
  --
  ...
}

自動仕訳パターンマスタ }o--|| 勘定科目マスタ : 借方科目
自動仕訳パターンマスタ }o--|| 勘定科目マスタ : 貸方科目
自動仕訳データ }o--|| 自動仕訳パターンマスタ

@enduml
```

### 設計のポイント

1. **パターンマッチング**: 商品グループ × 顧客グループで仕訳パターンを自動判定
2. **優先順位**: 複数パターンが該当する場合は優先順位で決定
3. **有効期間管理**: パターンの有効開始日・終了日で期間管理
4. **セット中心処理**: 大量データを効率的に処理
5. **中間テーブル**: 転記前の自動仕訳データを保持して確認可能
6. **処理履歴**: 自動仕訳処理の実行履歴を記録
7. **エラー管理**: パターン不適合時のエラー情報を記録

### ドメインロジック

| メソッド | 説明 |
|---------|------|
| `isValidAt(date)` | 指定日付に有効かどうかを判定 |
| `matches(productGroup, customerGroup)` | 商品グループと顧客グループにマッチするか判定 |

---

## 次章の予告

第18章では、勘定科目残高の設計に進みます。日次残高・月次残高の管理、残高更新のタイミング、合計残高試算表の出力を TDD で実装していきます。
