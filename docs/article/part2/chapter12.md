# 第12章：販売管理データ設計（B 社事例）

販売管理システムのデータベース設計を、食肉・食肉加工品の製造販売を行う B 社の事例を通じて実践的に学びます。本章では、実際のビジネスに基づいたマスタデータとトランザクションデータの設計・実装方法を解説します。

## B 社事例の全体像

B 社の販売管理システムに必要なデータ構造を、ビジネスモデルから段階的に設計します。

```plantuml
@startuml

title B 社の販売管理データ構造

rectangle "ビジネス分析" {
  card "会社概要" as overview
  card "組織構成" as org
  card "ビジネスモデル" as model
}

rectangle "データ設計" {
  card "マスタデータ" as master
  card "トランザクション" as trans
  card "コード体系" as code
}

rectangle "実装" {
  card "Seed データ" as seed
  card "整合性検証" as verify
}

overview --> org
org --> model
model --> master
master --> code
code --> trans
trans --> seed
seed --> verify

@enduml
```

### 本章の構成

| セクション | 内容 |
|-----------|------|
| **12.1 B 社の概要** | 会社プロファイル、事業環境、事業特徴 |
| **12.2 組織構成** | 組織図、部門階層、社員配置 |
| **12.3 ビジネスモデル** | ビジネスモデルキャンバス、取引先・商品構成 |
| **12.4 データ構造の設計** | マスタ構成、コード体系、ENUM マッピング |
| **12.5 Seed データの実装** | マスタ・トランザクションの投入実装 |

---

## 12.1 B 社の概要

### 会社プロファイル

| 項目 | 内容 |
|------|------|
| **社名** | B 社（架空） |
| **業種** | 食肉・食肉加工品の製造・販売業 |
| **資本金** | 3,000 万円 |
| **従業員数** | 45 名（うちパート従業員 21 名） |
| **事業所** | 本社、工場、直営小売店 1 店舗 |
| **年間販売額** | 約 9 億円（2021 年度） |
| **主要取扱商品** | 牛肉、豚肉、鶏肉、食肉加工品 |

### 沿革と事業環境

B 社は X 県の大都市近郊に立地し、高速道路のインターチェンジからも近い車の利便性が良いエリアに位置しています。

1955 年に食肉小売店として開業し、当時の食肉消費拡大の波に乗って順調に成長。1960 年代には地域の百貨店や近隣スーパーへの卸売事業を開始しました。

百貨店やスーパーとの取引実績から、B 社の商品はクオリティの高さに定評があり、仕入れ元からの信頼も厚く、良質な食肉を安定的に仕入れられる体制が整っています。

### 事業の特徴

```plantuml
@startuml

title B 社の5つの事業特徴

rectangle "事業特徴" {
  card "高品質な商品" as quality
  card "対面販売" as face
  card "多様な販路" as channel
  card "自社製造" as factory
  card "OEM 対応" as oem
}

note right of quality
  百貨店向け贈答用を含めた
  最高級品質の食肉・加工品
end note

note right of face
  直営小売店での
  顧客ニーズに合わせた接客販売
end note

note right of channel
  百貨店、スーパー、ホテル・旅館
  飲食店、観光施設、EC
end note

note right of factory
  工場でハム、ソーセージ
  ローストビーフ等を製造
end note

note right of oem
  相手先ブランドでの
  製造も受託
end note

@enduml
```

| 特徴 | 説明 |
|------|------|
| **高品質な商品** | 百貨店向けには贈答用を含めた最高級品質の食肉や食肉加工品 |
| **対面販売** | 直営小売店では顧客ニーズに合わせた接客販売 |
| **多様な販路** | 百貨店、スーパー、ホテル・旅館、飲食店、観光施設、EC |
| **自社製造** | 工場でハム、ソーセージ、ローストビーフ等の加工品を製造 |
| **OEM 対応** | 相手先ブランドでの製造も受託 |

---

## 12.2 組織構成

### 組織図

```plantuml
@startwbs
* B社
** 食肉製造・販売事業
*** 食肉加工部門
**** 牛肉・豚肉・鶏肉課
**** 食肉加工品課 (ハム・ソーセージ・ローストビーフなど)
*** 小売販売部門
**** 直営小売店課
**** 百貨店・スーパー向け販売課
*** 新規取引先開拓部門
**** ホテル・旅館向け課
**** 飲食店向け課
** 食肉加工品事業
*** 自社ブランド部門
**** 贈答用製品製造課
**** 道の駅・土産物製品販売課
*** 相手先ブランド製造(OEM)部門
**** 客先要望対応課
** コンサルティング事業
*** 顧客対応部門
**** メニュー提案課
**** 半加工商品提供課
@endwbs
```

### 部門マスタの階層構造

```
本社（000000）
├── 食肉製造・販売事業（100000）
│   ├── 食肉加工部門（110000）
│   │   ├── 牛肉・豚肉・鶏肉課（111000）
│   │   └── 食肉加工品課（112000）
│   ├── 小売販売部門（120000）
│   │   ├── 直営小売店課（121000）
│   │   └── 百貨店・スーパー向け販売課（122000）
│   └── 新規取引先開拓部門（130000）
│       ├── ホテル・旅館向け課（131000）
│       └── 飲食店向け課（132000）
├── 食肉加工品事業（200000）
│   ├── 自社ブランド部門（210000）
│   │   ├── 贈答用製品製造課（211000）
│   │   └── 道の駅・土産物製品販売課（212000）
│   └── 相手先ブランド製造(OEM)部門（220000）
│       └── 客先要望対応課（221000）
└── コンサルティング事業（300000）
    └── 顧客対応部門（310000）
        ├── メニュー提案課（311000）
        └── 半加工商品提供課（312000）
```

### 社員の配置

| 部門 | 正社員 | パート | 計 |
|------|--------|--------|-----|
| 経営層（本社） | 2 名 | - | 2 名 |
| 食肉製造・販売事業 | 8 名 | 7 名 | 15 名 |
| 食肉加工品事業 | 6 名 | 8 名 | 14 名 |
| コンサルティング事業 | 6 名 | 6 名 | 12 名 |
| 経理・総務等 | 2 名 | - | 2 名 |
| **合計** | **24 名** | **21 名** | **45 名** |

---

## 12.3 ビジネスモデル

### ビジネスモデルキャンバス

```plantuml
@startmindmap
* ビジネスモデル
** 内部環境
*** 顧客
**** 顧客セグメント
***** 百貨店
***** スーパー
***** ホテル・旅館
***** 飲食店
***** 個人消費者（直営小売店利用）
***** EC利用顧客
**** 顧客関係
***** 高品質の食品提供による信頼構築
***** 小売顧客との対面接客
***** 顧客ニーズに基づくカスタマイズサービス（飲食店向け）
***** オンライン顧客とのデジタルサポート
*** 価値
**** 価値提案
***** 高品質な食肉と加工品
***** 百貨店・観光向けギフト商品
***** 健康志向に適応した加工品（低脂肪・低塩分等）
***** 顧客ニーズにフィットした加工品
***** 持続可能性を考慮した地域密着型商品
**** チャネル
***** 直営小売店
***** 百貨店・スーパー
***** 観光地の道の駅や土産物店
***** 配送による顧客への納品
***** オンライン販売（EC）
*** インフラ
**** 主要活動
***** 食肉の仕入れ・加工・販売
***** 新規取引先の開拓
***** 品質保証と顧客満足の追求
***** ブランド管理とマーケティング
***** 環境保全活動（食品ロス削減）
**** 主要リソース
***** 自社工場
***** 高度な職人による加工技術
***** ブランド価値
***** トレーサビリティとデジタルツール
**** 主要パートナー
***** 地元農家や畜産業者
***** 観光業者、地元流通業者
***** 地域のオンラインプラットフォーム
*** 資金
**** 収益源
***** 加工品の販売収入
***** 高品質食肉の販売収入
***** オンライン販売収入
**** コスト構造
***** 食肉の仕入れコスト
***** 工場運営コスト（人件費・設備費）
***** マーケティング費用
***** デジタル推進関連費用
left side
** 外部環境
*** 競争
**** 他の地元業者・大手食肉卸売業者
**** 全国規模スーパーの競争優位性
**** 代替肉や植物由来製品業者
*** 政治・社会・技術
**** 地域食品産業振興政策
**** 健康志向の高まりに基づく食の選択
**** 加工技術や物流の効率化
**** 環境保護と持続可能性の推進
*** マクロ経済
**** 消費者物価の動向（価格競争）
**** 労働力不足による人件費増加
**** 地元経済への還元と連携
*** 市場
**** ローカル市場（地元住民）
**** 観光市場（道の駅利用の観光客）
**** 周辺地域への広域展開可能性
**** 健康志向顧客層へのリーチ
@endmindmap
```

### 得意先の分類

| グループ | 取引先例 | 特徴 |
|---------|---------|------|
| 百貨店 | 地域百貨店、X 県有名百貨店 | 贈答用高級品、ギフト需要 |
| スーパー | 地域スーパーチェーン、広域スーパーチェーン | 日常使いのカット肉・スライス肉 |
| ホテル・旅館 | シティホテル、温泉旅館 | 宴会・レストラン向け |
| 飲食店 | 焼肉レストラン、イタリアンレストラン | メニュー提案付き販売 |
| 観光施設 | 道の駅、観光センター | 土産物・贈答品 |

### 仕入先の分類

| グループ | 取引先例 | 特徴 |
|---------|---------|------|
| 食肉卸 | 地域食肉卸 A 社、地域食肉卸 B 社 | 牛肉・豚肉・鶏肉の安定供給 |
| 畜産業者 | 地域畜産農家、県内畜産組合 | 高品質な原材料 |

### 商品構成

| 分類 | 商品例 | 特徴 |
|------|--------|------|
| 牛肉 | 黒毛和牛サーロイン、ロース、カルビ、ヒレ、切り落とし | 高級品から日常使いまで |
| 豚肉 | 豚ロース、豚バラ、豚ヒレ、豚コマ、豚肩ロース | 幅広い用途 |
| 鶏肉 | 鶏もも、鶏むね、手羽先、手羽元、鶏ささみ | 健康志向にも対応 |
| 加工品 | ローストビーフ、ハム、ソーセージ、ベーコン、コロッケ | 自社工場製造 |

---

## 12.4 データ構造の設計

### マスタデータの構成

| データ種別 | 件数 | 内容 |
|-----------|------|------|
| 部門マスタ | 21 件 | 4 階層の組織構造 |
| 社員マスタ | 24 件 | 正社員 24 名 |
| 取引先マスタ | 14 件 | 得意先 10 件、仕入先 4 件 |
| 商品分類マスタ | 4 件 | 牛肉、豚肉、鶏肉、加工品 |
| 商品マスタ | 20 件 | 各分類 5 件ずつ |
| 倉庫マスタ | 3 件 | 本社倉庫、工場倉庫、外部委託倉庫 |

### コード体系

#### 商品コード体系

| 接頭辞 | 区分 | 例 |
|--------|------|-----|
| BEEF- | 牛肉 | BEEF-001 黒毛和牛サーロイン |
| PORK- | 豚肉 | PORK-001 豚ロース |
| CHKN- | 鶏肉 | CHKN-001 鶏もも |
| PROC- | 加工品 | PROC-001 ローストビーフ |

#### 取引先コード体系

| 接頭辞 | 区分 | 例 |
|--------|------|-----|
| CUS- | 得意先 | CUS-001 地域百貨店 |
| SUP- | 仕入先 | SUP-001 地域食肉卸 A 社 |

#### 部門コード体系

| コード | 部門名 | 階層 |
|--------|--------|------|
| 000000 | 本社 | 1 |
| 100000 | 食肉製造・販売事業 | 2 |
| 110000 | 食肉加工部門 | 3 |
| 111000 | 牛肉・豚肉・鶏肉課 | 4 |
| 112000 | 食肉加工品課 | 4 |
| 120000 | 小売販売部門 | 3 |
| 121000 | 直営小売店課 | 4 |
| 122000 | 百貨店・スーパー向け販売課 | 4 |
| 200000 | 食肉加工品事業 | 2 |
| 300000 | コンサルティング事業 | 2 |

### ENUM の日本語・英語マッピング

本システムでは、データベースの ENUM 値は日本語、Java の ENUM は英語で定義し、MyBatis の TypeHandler でマッピングを行います。

#### 取引先区分（PartnerType）

| 日本語 DB 値 | 英語 Java 値 |
|-------------|-------------|
| "顧客" | CUSTOMER |
| "仕入先" | SUPPLIER |

#### 商品区分（ProductType）

| 日本語 DB 値 | 英語 Java 値 |
|-------------|-------------|
| "商品" | PRODUCT |
| "製品" | MANUFACTURED |
| "サービス" | SERVICE |

#### 税区分（TaxType）

| 日本語 DB 値 | 英語 Java 値 |
|-------------|-------------|
| "標準税率" | STANDARD |
| "軽減税率" | REDUCED |
| "非課税" | EXEMPT |

#### 受注ステータス（OrderStatus）

| 日本語 DB 値 | 英語 Java 値 |
|-------------|-------------|
| "受付済" | RECEIVED |
| "引当済" | ALLOCATED |
| "出荷指示済" | SHIPMENT_INSTRUCTED |
| "出荷済" | SHIPPED |
| "キャンセル" | CANCELLED |

#### 出荷ステータス（ShipmentStatus）

| 日本語 DB 値 | 英語 Java 値 |
|-------------|-------------|
| "未出荷" | PENDING |
| "出荷済" | SHIPPED |
| "配達完了" | DELIVERED |

#### 発注ステータス（PurchaseStatus）

| 日本語 DB 値 | 英語 Java 値 |
|-------------|-------------|
| "発注済" | ORDERED |
| "入荷済" | RECEIVED |
| "検収済" | INSPECTED |
| "キャンセル" | CANCELLED |

#### 倉庫区分（WarehouseType）

| 日本語 DB 値 | 英語 Java 値 |
|-------------|-------------|
| "自社" | OWN |
| "外部" | EXTERNAL |
| "仮想" | VIRTUAL |

### データモデル ER 図

```plantuml
@startuml

title B 社 販売管理データモデル

' マスタ系
package "マスタデータ" {
  entity "部門マスタ" as dept {
    * 部門コード : VARCHAR(6)
    * 適用開始日 : DATE
    --
    部門名 : VARCHAR(100)
    部門パス : VARCHAR(500)
    階層 : INTEGER
  }

  entity "社員マスタ" as emp {
    * 社員コード : VARCHAR(10)
    * 適用開始日 : DATE
    --
    姓 : VARCHAR(50)
    名 : VARCHAR(50)
    部門コード : VARCHAR(6)
    雇用区分 : VARCHAR(20)
  }

  entity "取引先グループマスタ" as grp {
    * グループコード : VARCHAR(20)
    * 適用開始日 : DATE
    --
    グループ名 : VARCHAR(100)
  }

  entity "取引先マスタ" as partner {
    * 取引先コード : VARCHAR(20)
    * 適用開始日 : DATE
    --
    取引先名 : VARCHAR(100)
    取引先区分 : 取引先区分
    グループコード : VARCHAR(20)
  }

  entity "顧客マスタ" as customer {
    * 取引先コード : VARCHAR(20)
    * 適用開始日 : DATE
    --
    締日 : INTEGER
    回収サイト : INTEGER
    回収日 : INTEGER
    与信限度額 : NUMERIC
  }

  entity "仕入先マスタ" as supplier {
    * 取引先コード : VARCHAR(20)
    * 適用開始日 : DATE
    --
    締日 : INTEGER
    支払サイト : INTEGER
    支払日 : INTEGER
    リードタイム : INTEGER
  }

  entity "商品分類マスタ" as category {
    * 分類コード : VARCHAR(20)
    * 適用開始日 : DATE
    --
    分類名 : VARCHAR(100)
    分類パス : VARCHAR(500)
    階層 : INTEGER
  }

  entity "商品マスタ" as product {
    * 商品コード : VARCHAR(20)
    * 適用開始日 : DATE
    --
    商品名 : VARCHAR(200)
    分類コード : VARCHAR(20)
    商品区分 : 商品区分
    税区分 : 税区分
    標準売価 : NUMERIC
    標準原価 : NUMERIC
  }

  entity "倉庫マスタ" as warehouse {
    * 倉庫コード : VARCHAR(20)
    * 適用開始日 : DATE
    --
    倉庫名 : VARCHAR(100)
    所在地 : VARCHAR(200)
    倉庫区分 : 倉庫区分
  }
}

' リレーション
dept ||--o{ emp : 所属
grp ||--o{ partner : 分類
partner ||--o| customer : 拡張
partner ||--o| supplier : 拡張
category ||--o{ product : 分類

@enduml
```

---

## 12.5 Seed データの実装

### 実装方針

Seed データの投入にあたり、以下のポイントを考慮します。

| ポイント | 説明 |
|---------|------|
| **外部キー制約の考慮** | データ投入順序を依存関係に基づいて設計 |
| **複合キーの扱い** | 適用開始日を含む複合主キーへの対応 |
| **日本語テーブル名・カラム名** | MyBatis の resultMap でマッピング |
| **Spring Profile の活用** | `seed` プロファイルで実行環境を分離 |

### プロジェクト構造

```
src/
├── main/
│   └── java/
│       └── com/example/sales/
│           ├── domain/
│           │   └── model/
│           │       └── seed/
│           └── infrastructure/
│               └── datasource/
│                   └── seed/
│                       ├── SeedDataService.java
│                       ├── MasterDataSeeder.java
│                       ├── TransactionDataSeeder.java
│                       └── SeedRunner.java
└── test/
    └── java/
        └── com/example/sales/
            └── integration/
                └── SeedDataIntegrationTest.java
```

### SeedDataService の実装

<details>
<summary>SeedDataService.java</summary>

```java
package com.example.sales.infrastructure.datasource.seed;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class SeedDataService {

    private static final Logger log = LoggerFactory.getLogger(SeedDataService.class);

    private final SqlSessionFactory sqlSessionFactory;
    private final MasterDataSeeder masterDataSeeder;
    private final TransactionDataSeeder transactionDataSeeder;

    public SeedDataService(
            SqlSessionFactory sqlSessionFactory,
            MasterDataSeeder masterDataSeeder,
            TransactionDataSeeder transactionDataSeeder) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.masterDataSeeder = masterDataSeeder;
        this.transactionDataSeeder = transactionDataSeeder;
    }

    @Transactional
    public void seedAll() {
        log.info("========================================");
        log.info("販売管理システム Seed データ投入開始");
        log.info("========================================");

        LocalDate effectiveDate = LocalDate.of(2025, 1, 1);

        // 既存データの削除
        cleanAllData();

        // マスタデータの投入
        masterDataSeeder.seedAll(effectiveDate);

        // トランザクションデータの投入
        transactionDataSeeder.seedAll(effectiveDate);

        log.info("========================================");
        log.info("販売管理システム Seed データ投入完了!");
        log.info("========================================");
    }

    private void cleanAllData() {
        log.info("既存データを削除中...");

        try (SqlSession session = sqlSessionFactory.openSession()) {
            // トランザクションデータから削除（外部キー制約のため逆順）
            session.delete("seed.deleteAllPaymentDetails");
            session.delete("seed.deleteAllPayments");
            session.delete("seed.deleteAllReceiptDetails");
            session.delete("seed.deleteAllReceipts");
            session.delete("seed.deleteAllInvoiceDetails");
            session.delete("seed.deleteAllInvoices");
            session.delete("seed.deleteAllSalesDetails");
            session.delete("seed.deleteAllSales");
            session.delete("seed.deleteAllShipmentDetails");
            session.delete("seed.deleteAllShipments");
            session.delete("seed.deleteAllOrderDetails");
            session.delete("seed.deleteAllOrders");
            session.delete("seed.deleteAllPurchaseDetails");
            session.delete("seed.deleteAllPurchases");
            session.delete("seed.deleteAllStockMovements");
            session.delete("seed.deleteAllInventories");

            // マスタデータを削除
            session.delete("seed.deleteAllEmployees");
            session.delete("seed.deleteAllProducts");
            session.delete("seed.deleteAllProductCategories");
            session.delete("seed.deleteAllCustomerPrices");
            session.delete("seed.deleteAllCustomers");
            session.delete("seed.deleteAllSuppliers");
            session.delete("seed.deleteAllPartners");
            session.delete("seed.deleteAllPartnerGroups");
            session.delete("seed.deleteAllLocations");
            session.delete("seed.deleteAllWarehouses");
            session.delete("seed.deleteAllDepartments");

            session.commit();
        }

        log.info("既存データ削除完了");
    }
}
```

</details>

### MasterDataSeeder の実装

<details>
<summary>MasterDataSeeder.java（部門・倉庫・取引先）</summary>

```java
package com.example.sales.infrastructure.datasource.seed;

import com.example.sales.domain.model.master.*;
import com.example.sales.infrastructure.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class MasterDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(MasterDataSeeder.class);

    private final DepartmentMapper departmentMapper;
    private final WarehouseMapper warehouseMapper;
    private final LocationMapper locationMapper;
    private final PartnerGroupMapper partnerGroupMapper;
    private final PartnerMapper partnerMapper;
    private final CustomerMapper customerMapper;
    private final SupplierMapper supplierMapper;
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductMapper productMapper;
    private final EmployeeMapper employeeMapper;

    // コンストラクタ省略

    public void seedAll(LocalDate effectiveDate) {
        seedDepartments(effectiveDate);
        seedWarehouses(effectiveDate);
        seedLocations(effectiveDate);
        seedPartnerGroups(effectiveDate);
        seedPartners(effectiveDate);
        seedCustomers(effectiveDate);
        seedSuppliers(effectiveDate);
        seedProductCategories(effectiveDate);
        seedProducts(effectiveDate);
        seedEmployees(effectiveDate);
    }

    private void seedDepartments(LocalDate effectiveDate) {
        log.info("部門マスタを投入中...");

        List<Department> departments = List.of(
            // 本社
            new Department("000000", effectiveDate, "本社", "/000000", 1),

            // 食肉製造・販売事業
            new Department("100000", effectiveDate, "食肉製造・販売事業", "/000000/100000", 2),
            new Department("110000", effectiveDate, "食肉加工部門", "/000000/100000/110000", 3),
            new Department("111000", effectiveDate, "牛肉・豚肉・鶏肉課", "/000000/100000/110000/111000", 4),
            new Department("112000", effectiveDate, "食肉加工品課", "/000000/100000/110000/112000", 4),
            new Department("120000", effectiveDate, "小売販売部門", "/000000/100000/120000", 3),
            new Department("121000", effectiveDate, "直営小売店課", "/000000/100000/120000/121000", 4),
            new Department("122000", effectiveDate, "百貨店・スーパー向け販売課", "/000000/100000/120000/122000", 4),
            new Department("130000", effectiveDate, "新規取引先開拓部門", "/000000/100000/130000", 3),
            new Department("131000", effectiveDate, "ホテル・旅館向け課", "/000000/100000/130000/131000", 4),
            new Department("132000", effectiveDate, "飲食店向け課", "/000000/100000/130000/132000", 4),

            // 食肉加工品事業
            new Department("200000", effectiveDate, "食肉加工品事業", "/000000/200000", 2),
            new Department("210000", effectiveDate, "自社ブランド部門", "/000000/200000/210000", 3),
            new Department("211000", effectiveDate, "贈答用製品製造課", "/000000/200000/210000/211000", 4),
            new Department("212000", effectiveDate, "道の駅・土産物製品販売課", "/000000/200000/210000/212000", 4),
            new Department("220000", effectiveDate, "相手先ブランド製造(OEM)部門", "/000000/200000/220000", 3),
            new Department("221000", effectiveDate, "客先要望対応課", "/000000/200000/220000/221000", 4),

            // コンサルティング事業
            new Department("300000", effectiveDate, "コンサルティング事業", "/000000/300000", 2),
            new Department("310000", effectiveDate, "顧客対応部門", "/000000/300000/310000", 3),
            new Department("311000", effectiveDate, "メニュー提案課", "/000000/300000/310000/311000", 4),
            new Department("312000", effectiveDate, "半加工商品提供課", "/000000/300000/310000/312000", 4)
        );

        departments.forEach(departmentMapper::insert);
        log.info("部門マスタ {}件 投入完了", departments.size());
    }

    private void seedWarehouses(LocalDate effectiveDate) {
        log.info("倉庫マスタを投入中...");

        List<Warehouse> warehouses = List.of(
            new Warehouse("WH-HQ", effectiveDate, "本社倉庫", "本社1F", WarehouseType.COMPANY_OWNED),
            new Warehouse("WH-FAC", effectiveDate, "工場倉庫", "工場1F", WarehouseType.COMPANY_OWNED)
        );

        warehouses.forEach(warehouseMapper::insert);
        log.info("倉庫マスタ {}件 投入完了", warehouses.size());
    }

    private void seedPartnerGroups(LocalDate effectiveDate) {
        log.info("取引先グループマスタを投入中...");

        List<PartnerGroup> groups = List.of(
            new PartnerGroup("GRP-DEPT", effectiveDate, "百貨店グループ"),
            new PartnerGroup("GRP-SUPER", effectiveDate, "スーパーグループ"),
            new PartnerGroup("GRP-HOTEL", effectiveDate, "ホテル・旅館グループ"),
            new PartnerGroup("GRP-REST", effectiveDate, "飲食店グループ"),
            new PartnerGroup("GRP-TOUR", effectiveDate, "観光施設グループ"),
            new PartnerGroup("GRP-MEAT", effectiveDate, "食肉卸グループ"),
            new PartnerGroup("GRP-FARM", effectiveDate, "畜産業者グループ")
        );

        groups.forEach(partnerGroupMapper::insert);
        log.info("取引先グループマスタ {}件 投入完了", groups.size());
    }

    private void seedPartners(LocalDate effectiveDate) {
        log.info("取引先マスタを投入中...");

        List<Partner> partners = List.of(
            // 得意先（百貨店）
            new Partner("CUS-001", effectiveDate, "地域百貨店", PartnerType.CUSTOMER, "GRP-DEPT", effectiveDate),
            new Partner("CUS-002", effectiveDate, "X県有名百貨店", PartnerType.CUSTOMER, "GRP-DEPT", effectiveDate),
            // 得意先（スーパー）
            new Partner("CUS-003", effectiveDate, "地域スーパーチェーン", PartnerType.CUSTOMER, "GRP-SUPER", effectiveDate),
            new Partner("CUS-004", effectiveDate, "広域スーパーチェーン", PartnerType.CUSTOMER, "GRP-SUPER", effectiveDate),
            // 得意先（ホテル・旅館）
            new Partner("CUS-005", effectiveDate, "シティホテル", PartnerType.CUSTOMER, "GRP-HOTEL", effectiveDate),
            new Partner("CUS-006", effectiveDate, "温泉旅館", PartnerType.CUSTOMER, "GRP-HOTEL", effectiveDate),
            // 得意先（飲食店）
            new Partner("CUS-007", effectiveDate, "焼肉レストラン", PartnerType.CUSTOMER, "GRP-REST", effectiveDate),
            new Partner("CUS-008", effectiveDate, "イタリアンレストラン", PartnerType.CUSTOMER, "GRP-REST", effectiveDate),
            // 得意先（観光施設）
            new Partner("CUS-009", effectiveDate, "道の駅", PartnerType.CUSTOMER, "GRP-TOUR", effectiveDate),
            new Partner("CUS-010", effectiveDate, "観光センター", PartnerType.CUSTOMER, "GRP-TOUR", effectiveDate),
            // 仕入先（食肉卸）
            new Partner("SUP-001", effectiveDate, "地域食肉卸A社", PartnerType.SUPPLIER, "GRP-MEAT", effectiveDate),
            new Partner("SUP-002", effectiveDate, "地域食肉卸B社", PartnerType.SUPPLIER, "GRP-MEAT", effectiveDate),
            // 仕入先（畜産業者）
            new Partner("SUP-003", effectiveDate, "地域畜産農家", PartnerType.SUPPLIER, "GRP-FARM", effectiveDate),
            new Partner("SUP-004", effectiveDate, "県内畜産組合", PartnerType.SUPPLIER, "GRP-FARM", effectiveDate)
        );

        partners.forEach(partnerMapper::insert);
        log.info("取引先マスタ {}件 投入完了", partners.size());
    }
}
```

</details>

<details>
<summary>MasterDataSeeder.java（商品・社員）</summary>

```java
    private void seedProductCategories(LocalDate effectiveDate) {
        log.info("商品分類マスタを投入中...");

        List<ProductCategory> categories = List.of(
            new ProductCategory("CAT-BEEF", effectiveDate, "牛肉", "/CAT-BEEF", 1),
            new ProductCategory("CAT-PORK", effectiveDate, "豚肉", "/CAT-PORK", 1),
            new ProductCategory("CAT-CHKN", effectiveDate, "鶏肉", "/CAT-CHKN", 1),
            new ProductCategory("CAT-PROC", effectiveDate, "加工品", "/CAT-PROC", 1)
        );

        categories.forEach(productCategoryMapper::insert);
        log.info("商品分類マスタ {}件 投入完了", categories.size());
    }

    private void seedProducts(LocalDate effectiveDate) {
        log.info("商品マスタを投入中...");

        List<Product> products = List.of(
            // 牛肉
            new Product("BEEF-001", effectiveDate, "黒毛和牛サーロイン", "CAT-BEEF", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("8000"), new BigDecimal("5000")),
            new Product("BEEF-002", effectiveDate, "黒毛和牛ロース", "CAT-BEEF", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("6000"), new BigDecimal("3800")),
            new Product("BEEF-003", effectiveDate, "黒毛和牛カルビ", "CAT-BEEF", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("5500"), new BigDecimal("3500")),
            new Product("BEEF-004", effectiveDate, "黒毛和牛ヒレ", "CAT-BEEF", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("10000"), new BigDecimal("6500")),
            new Product("BEEF-005", effectiveDate, "黒毛和牛切り落とし", "CAT-BEEF", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("2500"), new BigDecimal("1500")),

            // 豚肉
            new Product("PORK-001", effectiveDate, "豚ロース", "CAT-PORK", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("1200"), new BigDecimal("750")),
            new Product("PORK-002", effectiveDate, "豚バラ", "CAT-PORK", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("980"), new BigDecimal("600")),
            new Product("PORK-003", effectiveDate, "豚ヒレ", "CAT-PORK", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("1500"), new BigDecimal("950")),
            new Product("PORK-004", effectiveDate, "豚コマ", "CAT-PORK", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("680"), new BigDecimal("400")),
            new Product("PORK-005", effectiveDate, "豚肩ロース", "CAT-PORK", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("1100"), new BigDecimal("700")),

            // 鶏肉
            new Product("CHKN-001", effectiveDate, "鶏もも", "CAT-CHKN", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("480"), new BigDecimal("280")),
            new Product("CHKN-002", effectiveDate, "鶏むね", "CAT-CHKN", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("380"), new BigDecimal("220")),
            new Product("CHKN-003", effectiveDate, "手羽先", "CAT-CHKN", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("350"), new BigDecimal("200")),
            new Product("CHKN-004", effectiveDate, "手羽元", "CAT-CHKN", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("320"), new BigDecimal("180")),
            new Product("CHKN-005", effectiveDate, "鶏ささみ", "CAT-CHKN", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("520"), new BigDecimal("320")),

            // 加工品
            new Product("PROC-001", effectiveDate, "ローストビーフ", "CAT-PROC", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("3500"), new BigDecimal("2000")),
            new Product("PROC-002", effectiveDate, "ロースハム", "CAT-PROC", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("1800"), new BigDecimal("1000")),
            new Product("PROC-003", effectiveDate, "あらびきソーセージ", "CAT-PROC", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("680"), new BigDecimal("400")),
            new Product("PROC-004", effectiveDate, "ベーコン", "CAT-PROC", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("980"), new BigDecimal("580")),
            new Product("PROC-005", effectiveDate, "手作りコロッケ", "CAT-PROC", effectiveDate,
                       ProductType.PRODUCT, TaxType.STANDARD, new BigDecimal("250"), new BigDecimal("120"))
        );

        products.forEach(productMapper::insert);
        log.info("商品マスタ {}件 投入完了", products.size());
    }

    private void seedEmployees(LocalDate effectiveDate) {
        log.info("社員マスタを投入中...");

        List<Employee> employees = List.of(
            // 経営層
            new Employee("EMP-001", effectiveDate, "山田", "太郎", "000000", effectiveDate, "EXEC"),
            new Employee("EMP-002", effectiveDate, "佐藤", "次郎", "000000", effectiveDate, "EXEC"),

            // 食肉製造・販売事業（正社員8名）
            new Employee("EMP-003", effectiveDate, "鈴木", "三郎", "111000", effectiveDate, "MANAGER"),
            new Employee("EMP-004", effectiveDate, "高橋", "四郎", "111000", effectiveDate, "REGULAR"),
            new Employee("EMP-005", effectiveDate, "田中", "五郎", "112000", effectiveDate, "MANAGER"),
            new Employee("EMP-006", effectiveDate, "伊藤", "六郎", "112000", effectiveDate, "REGULAR"),
            new Employee("EMP-007", effectiveDate, "渡辺", "七郎", "121000", effectiveDate, "MANAGER"),
            new Employee("EMP-008", effectiveDate, "山本", "八郎", "121000", effectiveDate, "REGULAR"),
            new Employee("EMP-009", effectiveDate, "中村", "九郎", "122000", effectiveDate, "REGULAR"),
            new Employee("EMP-010", effectiveDate, "小林", "十郎", "122000", effectiveDate, "REGULAR"),

            // 食肉加工品事業（正社員6名）
            new Employee("EMP-011", effectiveDate, "加藤", "一男", "211000", effectiveDate, "MANAGER"),
            new Employee("EMP-012", effectiveDate, "吉田", "二男", "211000", effectiveDate, "REGULAR"),
            new Employee("EMP-013", effectiveDate, "山口", "三男", "212000", effectiveDate, "MANAGER"),
            new Employee("EMP-014", effectiveDate, "松本", "四男", "212000", effectiveDate, "REGULAR"),
            new Employee("EMP-015", effectiveDate, "井上", "五男", "221000", effectiveDate, "MANAGER"),
            new Employee("EMP-016", effectiveDate, "木村", "六男", "221000", effectiveDate, "REGULAR"),

            // コンサルティング事業（正社員6名）
            new Employee("EMP-017", effectiveDate, "林", "一子", "311000", effectiveDate, "MANAGER"),
            new Employee("EMP-018", effectiveDate, "斎藤", "二子", "311000", effectiveDate, "REGULAR"),
            new Employee("EMP-019", effectiveDate, "清水", "三子", "311000", effectiveDate, "REGULAR"),
            new Employee("EMP-020", effectiveDate, "森", "四子", "312000", effectiveDate, "MANAGER"),
            new Employee("EMP-021", effectiveDate, "池田", "五子", "312000", effectiveDate, "REGULAR"),
            new Employee("EMP-022", effectiveDate, "橋本", "六子", "312000", effectiveDate, "REGULAR"),

            // 経理・総務（正社員2名）
            new Employee("EMP-023", effectiveDate, "阿部", "一郎", "000000", effectiveDate, "REGULAR"),
            new Employee("EMP-024", effectiveDate, "石川", "二郎", "000000", effectiveDate, "REGULAR")
        );

        employees.forEach(employeeMapper::insert);
        log.info("社員マスタ {}件 投入完了", employees.size());
    }
```

</details>

### TransactionDataSeeder の実装

<details>
<summary>TransactionDataSeeder.java</summary>

```java
package com.example.sales.infrastructure.datasource.seed;

import com.example.sales.domain.model.inventory.*;
import com.example.sales.domain.model.sales.*;
import com.example.sales.domain.model.purchase.*;
import com.example.sales.infrastructure.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class TransactionDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(TransactionDataSeeder.class);

    private final InventoryMapper inventoryMapper;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final ShipmentMapper shipmentMapper;
    private final ShipmentDetailMapper shipmentDetailMapper;
    private final SalesMapper salesMapper;
    private final SalesDetailMapper salesDetailMapper;
    private final PurchaseMapper purchaseMapper;
    private final PurchaseDetailMapper purchaseDetailMapper;

    // コンストラクタ省略

    public void seedAll(LocalDate effectiveDate) {
        seedInventories(effectiveDate);
        seedOrders(effectiveDate);
        seedShipments(effectiveDate);
        seedSales(effectiveDate);
        seedPurchases(effectiveDate);
    }

    private void seedInventories(LocalDate effectiveDate) {
        log.info("在庫情報を投入中...");

        List<Inventory> inventories = List.of(
            // 本社倉庫の在庫
            new Inventory("WH-HQ", "BEEF-001", effectiveDate,
                         new BigDecimal("50"), new BigDecimal("10")),
            new Inventory("WH-HQ", "BEEF-002", effectiveDate,
                         new BigDecimal("80"), new BigDecimal("15")),
            new Inventory("WH-HQ", "BEEF-003", effectiveDate,
                         new BigDecimal("100"), new BigDecimal("20")),
            new Inventory("WH-HQ", "PORK-001", effectiveDate,
                         new BigDecimal("200"), new BigDecimal("30")),
            new Inventory("WH-HQ", "PORK-002", effectiveDate,
                         new BigDecimal("250"), new BigDecimal("40")),
            new Inventory("WH-HQ", "CHKN-001", effectiveDate,
                         new BigDecimal("300"), new BigDecimal("50")),
            new Inventory("WH-HQ", "CHKN-002", effectiveDate,
                         new BigDecimal("350"), new BigDecimal("60")),

            // 工場倉庫の在庫（加工品）
            new Inventory("WH-FAC", "PROC-001", effectiveDate,
                         new BigDecimal("100"), new BigDecimal("20")),
            new Inventory("WH-FAC", "PROC-002", effectiveDate,
                         new BigDecimal("150"), new BigDecimal("30")),
            new Inventory("WH-FAC", "PROC-003", effectiveDate,
                         new BigDecimal("200"), new BigDecimal("40")),
            new Inventory("WH-FAC", "PROC-004", effectiveDate,
                         new BigDecimal("180"), new BigDecimal("35")),
            new Inventory("WH-FAC", "PROC-005", effectiveDate,
                         new BigDecimal("300"), new BigDecimal("50"))
        );

        inventories.forEach(inventoryMapper::insert);
        log.info("在庫情報 {}件 投入完了", inventories.size());
    }

    private void seedOrders(LocalDate effectiveDate) {
        log.info("受注データを投入中...");

        // 受注1（百貨店向け）
        Order order1 = new Order(
            "ORD-2025-001", LocalDate.of(2025, 1, 10),
            "CUS-001", effectiveDate, OrderStatus.CONFIRMED,
            "EMP-009", effectiveDate
        );
        orderMapper.insert(order1);
        orderDetailMapper.insert(new OrderDetail(
            "ORD-2025-001", 1, "BEEF-001", effectiveDate,
            new BigDecimal("10"), new BigDecimal("8000"), new BigDecimal("80000")
        ));
        orderDetailMapper.insert(new OrderDetail(
            "ORD-2025-001", 2, "PROC-001", effectiveDate,
            new BigDecimal("20"), new BigDecimal("3500"), new BigDecimal("70000")
        ));

        // 受注2（スーパー向け）
        Order order2 = new Order(
            "ORD-2025-002", LocalDate.of(2025, 1, 12),
            "CUS-003", effectiveDate, OrderStatus.CONFIRMED,
            "EMP-009", effectiveDate
        );
        orderMapper.insert(order2);
        orderDetailMapper.insert(new OrderDetail(
            "ORD-2025-002", 1, "PORK-001", effectiveDate,
            new BigDecimal("50"), new BigDecimal("1200"), new BigDecimal("60000")
        ));
        orderDetailMapper.insert(new OrderDetail(
            "ORD-2025-002", 2, "CHKN-001", effectiveDate,
            new BigDecimal("100"), new BigDecimal("480"), new BigDecimal("48000")
        ));

        // 受注3（ホテル向け）
        Order order3 = new Order(
            "ORD-2025-003", LocalDate.of(2025, 1, 15),
            "CUS-005", effectiveDate, OrderStatus.SHIPPED,
            "EMP-010", effectiveDate
        );
        orderMapper.insert(order3);
        orderDetailMapper.insert(new OrderDetail(
            "ORD-2025-003", 1, "BEEF-002", effectiveDate,
            new BigDecimal("30"), new BigDecimal("6000"), new BigDecimal("180000")
        ));
        orderDetailMapper.insert(new OrderDetail(
            "ORD-2025-003", 2, "BEEF-003", effectiveDate,
            new BigDecimal("25"), new BigDecimal("5500"), new BigDecimal("137500")
        ));

        log.info("受注データ 3件 投入完了");
    }

    private void seedShipments(LocalDate effectiveDate) {
        log.info("出荷データを投入中...");

        // 出荷1（受注3に対応）
        Shipment shipment1 = new Shipment(
            "SHP-2025-001", LocalDate.of(2025, 1, 16),
            "ORD-2025-003", "CUS-005", effectiveDate,
            ShipmentStatus.SHIPPED, "WH-HQ", effectiveDate
        );
        shipmentMapper.insert(shipment1);
        shipmentDetailMapper.insert(new ShipmentDetail(
            "SHP-2025-001", 1, "BEEF-002", effectiveDate,
            new BigDecimal("30")
        ));
        shipmentDetailMapper.insert(new ShipmentDetail(
            "SHP-2025-001", 2, "BEEF-003", effectiveDate,
            new BigDecimal("25")
        ));

        log.info("出荷データ 1件 投入完了");
    }

    private void seedSales(LocalDate effectiveDate) {
        log.info("売上データを投入中...");

        // 売上1（出荷1に対応）
        Sales sales1 = new Sales(
            "SAL-2025-001", LocalDate.of(2025, 1, 16),
            "SHP-2025-001", "CUS-005", effectiveDate,
            new BigDecimal("317500"), new BigDecimal("31750"),
            new BigDecimal("349250")
        );
        salesMapper.insert(sales1);
        salesDetailMapper.insert(new SalesDetail(
            "SAL-2025-001", 1, "BEEF-002", effectiveDate,
            new BigDecimal("30"), new BigDecimal("6000"), new BigDecimal("180000")
        ));
        salesDetailMapper.insert(new SalesDetail(
            "SAL-2025-001", 2, "BEEF-003", effectiveDate,
            new BigDecimal("25"), new BigDecimal("5500"), new BigDecimal("137500")
        ));

        log.info("売上データ 1件 投入完了");
    }

    private void seedPurchases(LocalDate effectiveDate) {
        log.info("発注データを投入中...");

        // 発注1（食肉卸A社から）
        Purchase purchase1 = new Purchase(
            "PUR-2025-001", LocalDate.of(2025, 1, 8),
            "SUP-001", effectiveDate, PurchaseStatus.RECEIVED,
            LocalDate.of(2025, 1, 12)
        );
        purchaseMapper.insert(purchase1);
        purchaseDetailMapper.insert(new PurchaseDetail(
            "PUR-2025-001", 1, "BEEF-001", effectiveDate,
            new BigDecimal("30"), new BigDecimal("5000"), new BigDecimal("150000")
        ));
        purchaseDetailMapper.insert(new PurchaseDetail(
            "PUR-2025-001", 2, "BEEF-002", effectiveDate,
            new BigDecimal("50"), new BigDecimal("3800"), new BigDecimal("190000")
        ));

        // 発注2（畜産農家から）
        Purchase purchase2 = new Purchase(
            "PUR-2025-002", LocalDate.of(2025, 1, 10),
            "SUP-003", effectiveDate, PurchaseStatus.ORDERED,
            LocalDate.of(2025, 1, 20)
        );
        purchaseMapper.insert(purchase2);
        purchaseDetailMapper.insert(new PurchaseDetail(
            "PUR-2025-002", 1, "PORK-001", effectiveDate,
            new BigDecimal("100"), new BigDecimal("750"), new BigDecimal("75000")
        ));
        purchaseDetailMapper.insert(new PurchaseDetail(
            "PUR-2025-002", 2, "CHKN-001", effectiveDate,
            new BigDecimal("200"), new BigDecimal("280"), new BigDecimal("56000")
        ));

        log.info("発注データ 2件 投入完了");
    }
}
```

</details>

### Seed データ実行方法

#### Gradle タスクの設定

```kotlin
// build.gradle.kts（default プロファイルで実行）
tasks.register<JavaExec>("seedData") {
    group = "application"
    description = "Seed データを投入する（default プロファイル）"
    mainClass.set("com.example.sms.Application")
    classpath = sourceSets["main"].runtimeClasspath
}
```

#### 実行コマンド

```bash
# Gradle タスクで実行
./gradlew seedData

# または直接 Java で実行（default プロファイル）
java -jar build/libs/sms-backend.jar
```

#### 実行結果の例

```
========================================
販売管理システム Seed データ投入開始
========================================
既存データを削除中...
既存データ削除完了
部門マスタを投入中...
部門マスタ 21件 投入完了
倉庫マスタを投入中...
倉庫マスタ 2件 投入完了
ロケーションマスタを投入中...
ロケーションマスタ 9件 投入完了
取引先グループマスタを投入中...
取引先グループマスタ 7件 投入完了
取引先マスタを投入中...
取引先マスタ 14件 投入完了
顧客マスタを投入中...
顧客マスタ 10件 投入完了
仕入先マスタを投入中...
仕入先マスタ 4件 投入完了
商品分類マスタを投入中...
商品分類マスタ 4件 投入完了
商品マスタを投入中...
商品マスタ 20件 投入完了
社員マスタを投入中...
社員マスタ 24件 投入完了
在庫情報を投入中...
在庫情報 12件 投入完了
受注データを投入中...
受注データ 3件 投入完了
出荷データを投入中...
出荷データ 1件 投入完了
売上データを投入中...
売上データ 1件 投入完了
発注データを投入中...
発注データ 2件 投入完了
========================================
販売管理システム Seed データ投入完了!
========================================
```

### データの検証と活用

<details>
<summary>SeedDataIntegrationTest.java</summary>

```java
package com.example.sales.integration;

import com.example.sales.domain.model.master.*;
import com.example.sales.domain.model.sales.*;
import com.example.sales.infrastructure.datasource.seed.SeedDataService;
import com.example.sales.infrastructure.mapper.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@DisplayName("Seed データ整合性チェック")
class SeedDataIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("sales_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private SeedDataService seedDataService;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PartnerMapper partnerMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private OrderMapper orderMapper;

    @BeforeEach
    void seedData() {
        seedDataService.seedAll();
    }

    @Nested
    @DisplayName("マスタデータの妥当性検証")
    class MasterDataValidation {

        @Test
        @DisplayName("すべての部門が階層構造を持つ")
        void allDepartmentsHaveHierarchy() {
            List<Department> departments = departmentMapper.findAll();

            assertThat(departments).hasSize(21);
            for (Department dept : departments) {
                assertThat(dept.getPath()).isNotBlank();
                assertThat(dept.getLayer()).isPositive();
            }
        }

        @Test
        @DisplayName("すべての商品が分類に所属している")
        void allProductsBelongToCategory() {
            List<Product> products = productMapper.findAll();

            assertThat(products).hasSize(20);
            for (Product product : products) {
                assertThat(product.getCategoryCode()).isNotBlank();
            }
        }

        @Test
        @DisplayName("得意先と仕入先が正しく区分されている")
        void customersAndSuppliersAreCorrectlyClassified() {
            List<Partner> customers = partnerMapper.findByType(PartnerType.CUSTOMER);
            List<Partner> suppliers = partnerMapper.findByType(PartnerType.SUPPLIER);

            assertThat(customers).hasSize(10);
            assertThat(suppliers).hasSize(4);
        }
    }

    @Nested
    @DisplayName("在庫数量の正確性検証")
    class InventoryValidation {

        @Test
        @DisplayName("在庫数量が0以上である")
        void inventoryQuantityIsNonNegative() {
            List<Inventory> inventories = inventoryMapper.findAll();

            assertThat(inventories).isNotEmpty();
            for (Inventory inventory : inventories) {
                assertThat(inventory.getQuantity()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
                assertThat(inventory.getAllocatedQuantity()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
            }
        }

        @Test
        @DisplayName("引当数量が在庫数量を超えていない")
        void allocatedQuantityDoesNotExceedInventory() {
            List<Inventory> inventories = inventoryMapper.findAll();

            for (Inventory inventory : inventories) {
                assertThat(inventory.getAllocatedQuantity())
                    .isLessThanOrEqualTo(inventory.getQuantity());
            }
        }
    }

    @Nested
    @DisplayName("受注データの整合性確認")
    class OrderIntegrity {

        @Test
        @DisplayName("受注に対応する顧客が存在する")
        void orderHasValidCustomer() {
            List<Order> orders = orderMapper.findAll();

            assertThat(orders).isNotEmpty();
            for (Order order : orders) {
                Partner customer = partnerMapper.findByCode(order.getCustomerCode());
                assertThat(customer).isNotNull();
                assertThat(customer.getPartnerType()).isEqualTo(PartnerType.CUSTOMER);
            }
        }

        @Test
        @DisplayName("受注明細の金額が正しく計算されている")
        void orderDetailAmountIsCorrectlyCalculated() {
            List<Order> orders = orderMapper.findAll();

            for (Order order : orders) {
                for (OrderDetail detail : order.getDetails()) {
                    BigDecimal expectedAmount = detail.getUnitPrice()
                        .multiply(detail.getQuantity());
                    assertThat(detail.getAmount()).isEqualByComparingTo(expectedAmount);
                }
            }
        }
    }
}
```

</details>

---

## 第12章のまとめ

B 社の事例を通じて、販売管理システムのデータ設計と Seed データ実装を行いました。

### 実装したデータ

| カテゴリ | 内容 |
|---------|------|
| **マスタデータ** | 部門 21 件、取引先 14 件、商品 20 件、社員 24 件、倉庫 3 件 |
| **トランザクション** | 受注 3 件、在庫 20 件 |
| **備考** | 出荷・売上データは受注明細への依存関係が複雑なため、初期シードでは投入しない |

### B 社の事業特徴とデータ設計への反映

| 特徴 | データ設計への反映 |
|------|-------------------|
| 多様な販路 | 取引先グループによる分類管理（7 グループ） |
| 自社製造能力 | 工場倉庫と加工品カテゴリの分離 |
| 高品質へのこだわり | 商品マスタでの標準売価・原価管理 |
| 地域密着 | 食肉卸・畜産業者の仕入先分類 |

### 技術的なポイント

| ポイント | 内容 |
|---------|------|
| **外部キー制約** | マスタ → トランザクションの順で投入 |
| **複合キー** | 適用開始日を含む複合主キーへの対応 |
| **日本語テーブル名** | MyBatis でダブルクォートで囲む |
| **ENUM マッピング** | 日本語 DB 値 ↔ 英語 Java 値の変換 |
| **Spring Profile** | `seed` プロファイルで本番誤実行を防止 |

### ER 図（本章で扱ったテーブル）

```plantuml
@startuml

title 第12章 データ構造 ER 図

entity "部門マスタ" as dept {
  * 部門コード
  * 適用開始日
  --
  部門名
  部門パス
  階層
}

entity "社員マスタ" as emp {
  * 社員コード
  * 適用開始日
  --
  姓
  名
  部門コード
}

entity "取引先グループマスタ" as grp {
  * グループコード
  * 適用開始日
  --
  グループ名
}

entity "取引先マスタ" as partner {
  * 取引先コード
  * 適用開始日
  --
  取引先名
  取引先区分
  グループコード
}

entity "商品分類マスタ" as cat {
  * 分類コード
  * 適用開始日
  --
  分類名
}

entity "商品マスタ" as prod {
  * 商品コード
  * 適用開始日
  --
  商品名
  分類コード
  標準売価
  標準原価
}

entity "倉庫マスタ" as wh {
  * 倉庫コード
  * 適用開始日
  --
  倉庫名
  倉庫区分
}

entity "在庫データ" as inv {
  * 倉庫コード
  * 商品コード
  * 基準日
  --
  在庫数量
  引当数量
}

entity "受注データ" as order {
  * 受注番号
  --
  受注日
  顧客コード
  担当者コード
  受注ステータス
}

entity "受注明細データ" as od {
  * 受注番号
  * 明細番号
  --
  商品コード
  数量
  単価
  金額
}

dept ||--o{ emp
grp ||--o{ partner
cat ||--o{ prod
wh ||--o{ inv
prod ||--o{ inv
partner ||--o{ order
order ||--|{ od
prod ||--o{ od

@enduml
```

次の第13章では、このデータモデルを活用した API サービスの実装に進みます。
