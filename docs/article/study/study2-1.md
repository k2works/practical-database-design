# 実践データベース設計：販売管理システム 研究 1 - モノリスサービスの実装

## はじめに

本研究では、API サーバー構成（第13章）とは異なるアプローチとして、**モノリスアーキテクチャ**による販売管理システムを実装します。UI（テンプレートエンジン）、ビジネスロジック、データベースアクセスがすべて同一サーバー内で動作する、伝統的かつ堅実なアーキテクチャです。

---

## 第14章：モノリスアーキテクチャの基礎

### 14.1 モノリスアーキテクチャとは

**モノリス（Monolith / Monolithic Architecture）**とは、「API」と「UI」と「ビジネスロジック」が **1つの実行ユニット**（1つのプロジェクトやバイナリ）にまとまっているアーキテクチャを指します。

```plantuml
@startuml monolith_architecture
!define RECTANGLE class

title モノリスアーキテクチャ

package "モノリスサーバー (単一の実行ユニット)" {

  package "プレゼンテーション層" {
    [Controller]
    [Thymeleaf テンプレート]
    [静的リソース (CSS/JS)]
  }

  package "ビジネスロジック層" {
    [Application Service]
    [Domain Model]
    [Domain Service]
  }

  package "データアクセス層" {
    [Repository]
    [MyBatis Mapper]
  }
}

database "PostgreSQL" as DB

actor "ブラウザ" as Browser

Browser --> [Controller] : HTTP リクエスト
[Controller] --> [Thymeleaf テンプレート] : モデル渡し
[Thymeleaf テンプレート] --> Browser : HTML レスポンス
[Controller] --> [Application Service]
[Application Service] --> [Domain Model]
[Application Service] --> [Repository]
[Repository] --> [MyBatis Mapper]
[MyBatis Mapper] --> DB

note right of "モノリスサーバー (単一の実行ユニット)"
  すべてが1つのプロセスで動作
  - 同一JVMで実行
  - 同一デプロイユニット
  - 密結合だが開発・運用がシンプル
end note

@enduml
```

**モノリスの主な特徴：**

| 特徴 | 説明 |
|------|------|
| **単一デプロイ** | アプリケーション全体が1つのアーティファクト（JAR/WAR）としてデプロイ |
| **同一プロセス** | UI、ビジネスロジック、データアクセスが同じ JVM で動作 |
| **テンプレートエンジン** | サーバーサイドで HTML を生成してブラウザに返却 |
| **セッション管理** | サーバーサイドでユーザーセッションを管理 |
| **トランザクション境界** | 単一プロセス内でのローカルトランザクション |

---

### 14.2 API サーバー vs モノリス

```plantuml
@startuml api_vs_monolith
!define RECTANGLE class

left to right direction

package "API サーバー構成（疎結合）" {
  package "フロントエンド" as FE {
    [React/Vue/Angular]
    [SPA]
  }

  package "バックエンド" as BE {
    [REST API]
    [JSON レスポンス]
  }

  database "DB1" as DB_API

  FE --> BE : HTTP/JSON
  BE --> DB_API
}

package "モノリス構成（密結合）" {
  package "モノリスサーバー" as Mono {
    [Controller + View]
    [テンプレートエンジン]
    [ビジネスロジック]
    [データアクセス]
  }

  database "DB2" as DB_Mono

  Mono --> DB_Mono
}

actor "ブラウザ (API)" as Browser1
actor "ブラウザ (Mono)" as Browser2

Browser1 --> FE
Browser2 --> Mono : HTTP (HTML)

@enduml
```

| 観点 | API サーバー構成 | モノリス構成 |
|------|-----------------|-------------|
| **結合度** | 疎結合（フロントエンドとバックエンドが分離） | 密結合（すべてが同一サーバー） |
| **フロントエンド** | SPA（React/Vue/Angular） | テンプレートエンジン（Thymeleaf） |
| **通信方式** | REST API（JSON） | サーバーサイドレンダリング（HTML） |
| **開発チーム** | フロント/バックエンドで分業可能 | フルスタックで開発 |
| **デプロイ** | 別々にデプロイ可能 | 単一アーティファクトをデプロイ |
| **スケーリング** | 個別にスケール可能 | 全体をスケール |
| **複雑さ** | API 設計・認証・CORS などが必要 | シンプル、設定が少ない |
| **初期開発速度** | 環境構築に時間がかかる | 素早く開発開始できる |
| **SEO** | SSR/SSG が必要な場合あり | サーバーサイドレンダリングで SEO フレンドリー |

### 14.3 モノリスを選択すべき場面

**モノリスが適している状況：**

1. **小〜中規模のチーム**：専門のフロントエンドチームがいない場合
2. **社内システム**：SEO 不要、限られたユーザー数
3. **業務アプリケーション**：複雑な業務フローをサーバーサイドで処理
4. **迅速な開発**：MVP やプロトタイプの素早い構築
5. **運用コスト重視**：インフラ構成をシンプルに保ちたい場合

---

### 14.4 モノリスにおけるヘキサゴナルアーキテクチャ

モノリスであっても、ヘキサゴナルアーキテクチャ（Ports and Adapters）を採用することで、保守性の高い設計を実現できます。

```plantuml
@startuml hexagonal_monolith
!define RECTANGLE class

package "Hexagonal Architecture (モノリス版)" {

  RECTANGLE "Application Core\n(Domain + Use Cases)" as core {
    - Product (商品)
    - Partner (取引先)
    - SalesOrder (受注)
    - Shipment (出荷)
    - Invoice (請求)
    - ProductUseCase
    - SalesOrderUseCase
    - InvoiceUseCase
  }

  RECTANGLE "Input Adapters\n(Driving Side)" as input {
    - Thymeleaf Controller
    - フォーム処理
    - セッション管理
    - リクエスト検証
  }

  RECTANGLE "Output Adapters\n(Driven Side)" as output {
    - MyBatis Repository
    - Database Access
    - Entity Mapping
    - 帳票出力
  }
}

input --> core : "Input Ports\n(Use Cases)"
core --> output : "Output Ports\n(Repository Interfaces)"

note top of core
  純粋なビジネスロジック
  UI 技術（Thymeleaf）に依存しない
  テスト可能な状態を維持
end note

note left of input
  HTML フォームからの入力
  サーバーサイドレンダリング
  セッションベースの認証
end note

note right of output
  PostgreSQL + MyBatis
  帳票生成（PDF/Excel）
  ファイル出力
end note

@enduml
```

---

### 14.5 ディレクトリ構成

```
apps/sms/backend/src/main/java/com/example/sms/
├── domain/                       # ドメイン層（API版と共通）
│   ├── model/
│   │   ├── product/
│   │   ├── partner/
│   │   ├── sales/
│   │   ├── shipping/
│   │   ├── invoice/
│   │   └── receipt/
│   └── exception/
│
├── application/                  # アプリケーション層（API版と共通）
│   ├── port/
│   │   ├── in/                  # Input Port（ユースケース）
│   │   └── out/                 # Output Port（リポジトリ）
│   └── service/
│
├── infrastructure/
│   ├── out/
│   │   └── persistence/         # Output Adapter（DB実装）
│   │       ├── mapper/
│   │       ├── repository/
│   │       └── typehandler/
│   └── in/
│       └── web/                 # Input Adapter（Web実装）
│           ├── controller/      # Thymeleaf Controller
│           ├── form/            # フォームオブジェクト
│           └── helper/          # ビューヘルパー
│
└── config/

apps/sms/backend/src/main/resources/
├── templates/                    # Thymeleaf テンプレート
│   ├── layout/                   # 共通レイアウト
│   ├── products/                 # 商品マスタ画面
│   ├── partners/                 # 取引先マスタ画面
│   ├── orders/                   # 受注画面
│   ├── shipments/                # 出荷画面
│   └── invoices/                 # 請求画面
├── static/                       # 静的リソース
│   ├── css/
│   ├── js/
│   └── images/
├── mapper/                       # MyBatis Mapper XML
└── messages.properties           # メッセージリソース
```

---

### 14.6 技術スタックの導入

#### build.gradle.kts

<details>
<summary>コード例: build.gradle.kts</summary>

```kotlin
plugins {
    id("java")
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    // Spring Boot Web（REST APIではなくMVCとして使用）
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Thymeleaf（テンプレートエンジン）
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.4.0")

    // htmx（モダンなインタラクティブUI）
    // CDN から読み込むため依存関係は不要

    // MyBatis
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.4")

    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Webjars（Bootstrap等のフロントエンドライブラリ）
    implementation("org.webjars:bootstrap:5.3.3")
    implementation("org.webjars:webjars-locator-core:0.59")

    // 帳票出力
    implementation("org.apache.poi:poi-ooxml:5.3.0")  // Excel
    implementation("com.itextpdf:itext7-core:8.0.5")  // PDF

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}
```

</details>

---

### 14.7 共通レイアウト（layout/default.html）

<details>
<summary>コード例: layout/default.html</summary>

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title layout:title-pattern="$CONTENT_TITLE - $LAYOUT_TITLE">販売管理システム</title>

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" th:href="@{/webjars/bootstrap/css/bootstrap.min.css}">

    <!-- カスタム CSS -->
    <link rel="stylesheet" th:href="@{/css/style.css}">

    <!-- htmx（部分更新用） -->
    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
</head>
<body>
    <!-- ナビゲーションバー -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
            <a class="navbar-brand" th:href="@{/}">販売管理システム</a>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">マスタ</a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" th:href="@{/products}">商品マスタ</a></li>
                            <li><a class="dropdown-item" th:href="@{/partners}">取引先マスタ</a></li>
                        </ul>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">販売</a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" th:href="@{/orders}">受注</a></li>
                            <li><a class="dropdown-item" th:href="@{/shipments}">出荷</a></li>
                        </ul>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">債権</a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" th:href="@{/invoices}">請求</a></li>
                            <li><a class="dropdown-item" th:href="@{/receipts}">入金</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- フラッシュメッセージ -->
    <div class="container mt-3">
        <div th:if="${successMessage}" class="alert alert-success alert-dismissible fade show">
            <span th:text="${successMessage}"></span>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <div th:if="${errorMessage}" class="alert alert-danger alert-dismissible fade show">
            <span th:text="${errorMessage}"></span>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </div>

    <!-- メインコンテンツ -->
    <main class="container mt-4">
        <div layout:fragment="content"></div>
    </main>

    <!-- Bootstrap JS -->
    <script th:src="@{/webjars/bootstrap/js/bootstrap.bundle.min.js}"></script>
    <th:block layout:fragment="scripts"></th:block>
</body>
</html>
```

</details>

---

### 14.8 htmx による部分更新

モノリスアプリケーションでも、**htmx** を使用することで SPA のような操作性を実現できます。

```plantuml
@startuml htmx_flow
title htmx による部分更新

actor "ブラウザ" as Browser
participant "htmx" as htmx
participant "Controller" as Controller
participant "Thymeleaf\nフラグメント" as Fragment

Browser -> htmx : ボタンクリック
htmx -> Controller : AJAX リクエスト\n(hx-get/hx-post)
Controller -> Fragment : フラグメントを\nレンダリング
Fragment --> Controller : HTML フラグメント
Controller --> htmx : HTML レスポンス
htmx -> Browser : DOM を部分更新\n(hx-target)

note right of htmx
  htmx は特別な属性（hx-*）を
  使って AJAX 通信を行う
  JavaScript を書かずに
  インタラクティブな UI を実現
end note

@enduml
```

#### htmx の基本的な使い方

<details>
<summary>コード例: htmx による商品検索</summary>

```html
<!-- 商品検索（部分更新） -->
<input type="text" class="form-control"
       hx-get="/products/search"
       hx-trigger="keyup changed delay:300ms"
       hx-target="#search-results"
       hx-indicator="#loading">
<span id="loading" class="htmx-indicator">検索中...</span>

<div id="search-results">
    <!-- Controller から返される HTML フラグメント -->
</div>
```

</details>

---

## 第15章：マスタの実装

### 15.1 商品マスタ画面の設計

| 画面 | URL | メソッド | 説明 |
|------|-----|---------|------|
| 商品一覧 | /products | GET | 商品の検索・一覧表示 |
| 商品詳細 | /products/{productCode} | GET | 商品の詳細表示 |
| 商品登録 | /products/new | GET | 登録フォーム表示 |
| 商品登録処理 | /products | POST | 登録処理 |
| 商品編集 | /products/{productCode}/edit | GET | 編集フォーム表示 |
| 商品更新処理 | /products/{productCode} | POST | 更新処理（PUT 代替） |
| 商品削除処理 | /products/{productCode}/delete | POST | 削除処理（DELETE 代替） |

### 15.2 フォームオブジェクトの設計

<details>
<summary>コード例: ProductForm.java</summary>

```java
package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.CreateProductCommand;
import com.example.sms.domain.model.product.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品登録・編集フォーム
 */
@Data
public class ProductForm {

    @NotBlank(message = "商品コードは必須です")
    @Size(max = 20, message = "商品コードは20文字以内で入力してください")
    private String productCode;

    @NotBlank(message = "商品名は必須です")
    @Size(max = 100, message = "商品名は100文字以内で入力してください")
    private String productName;

    @NotNull(message = "商品区分は必須です")
    private ProductCategory category;

    @Positive(message = "単価は正の数で入力してください")
    private BigDecimal unitPrice;

    /**
     * フォームをコマンドに変換
     */
    public CreateProductCommand toCommand() {
        return CreateProductCommand.builder()
            .productCode(this.productCode)
            .productName(this.productName)
            .category(this.category)
            .unitPrice(this.unitPrice)
            .build();
    }

    /**
     * エンティティからフォームを生成
     */
    public static ProductForm from(Product product) {
        ProductForm form = new ProductForm();
        form.setProductCode(product.getProductCode());
        form.setProductName(product.getProductName());
        form.setCategory(product.getCategory());
        form.setUnitPrice(product.getUnitPrice());
        return form;
    }
}
```

</details>

### 15.3 商品 Controller の TDD 実装

<details>
<summary>コード例: ProductControllerTest.java</summary>

```java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("商品マスタ画面")
class ProductControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("sms_test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("商品一覧画面を表示できる")
    void shouldDisplayProductList() throws Exception {
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(view().name("products/list"))
            .andExpect(model().attributeExists("products"));
    }

    @Test
    @DisplayName("商品を登録できる")
    void shouldCreateProduct() throws Exception {
        mockMvc.perform(post("/products")
                .param("productCode", "NEW-001")
                .param("productName", "新規商品")
                .param("category", "FINISHED_GOODS")
                .param("unitPrice", "1000"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/products"))
            .andExpect(flash().attribute("successMessage", containsString("登録")));
    }
}
```

</details>

### 15.4 商品 Controller

<details>
<summary>コード例: ProductController.java</summary>

```java
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public String list(@RequestParam(required = false) ProductCategory category, Model model) {
        List<Product> products = category != null
            ? productUseCase.findByCategory(category)
            : productUseCase.findAll();
        model.addAttribute("products", products);
        model.addAttribute("categories", ProductCategory.values());
        return "products/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ProductForm());
        model.addAttribute("categories", ProductCategory.values());
        return "products/new";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ProductForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", ProductCategory.values());
            return "products/new";
        }

        productUseCase.createProduct(form.toCommand());
        redirectAttributes.addFlashAttribute("successMessage", "商品を登録しました");
        return "redirect:/products";
    }

    @GetMapping("/{productCode}/edit")
    public String editForm(@PathVariable String productCode, Model model) {
        Product product = productUseCase.findByCode(productCode)
            .orElseThrow(() -> new ProductNotFoundException(productCode));
        model.addAttribute("form", ProductForm.from(product));
        model.addAttribute("categories", ProductCategory.values());
        return "products/edit";
    }

    @PostMapping("/{productCode}")
    public String update(
            @PathVariable String productCode,
            @Valid @ModelAttribute("form") ProductForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", ProductCategory.values());
            return "products/edit";
        }

        productUseCase.updateProduct(productCode, form.toCommand());
        redirectAttributes.addFlashAttribute("successMessage", "商品を更新しました");
        return "redirect:/products/" + productCode;
    }

    @PostMapping("/{productCode}/delete")
    public String delete(@PathVariable String productCode, RedirectAttributes redirectAttributes) {
        productUseCase.deleteProduct(productCode);
        redirectAttributes.addFlashAttribute("successMessage", "商品を削除しました");
        return "redirect:/products";
    }
}
```

</details>

---

## 第16章：トランザクションの実装

### 16.1 受注業務画面の実装

#### 受注 Controller

<details>
<summary>コード例: OrderController.java</summary>

```java
@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;
    private final ProductUseCase productUseCase;
    private final PartnerUseCase partnerUseCase;

    @GetMapping
    public String list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerCode,
            Model model) {

        List<Order> orders = orderUseCase.getOrders(status, customerCode);
        model.addAttribute("orders", orders);
        model.addAttribute("customers", partnerUseCase.getCustomers());
        return "orders/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new OrderForm());
        model.addAttribute("customers", partnerUseCase.getCustomers());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "orders/new";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") OrderForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("customers", partnerUseCase.getCustomers());
            model.addAttribute("products", productUseCase.getAllProducts());
            return "orders/new";
        }

        Order order = orderUseCase.createOrder(form.toCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "受注「" + order.getOrderNumber() + "」を登録しました");
        return "redirect:/orders";
    }

    @PostMapping("/{orderNumber}/confirm")
    public String confirm(@PathVariable String orderNumber, RedirectAttributes redirectAttributes) {
        orderUseCase.confirmOrder(orderNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "受注「" + orderNumber + "」を確定しました");
        return "redirect:/orders/" + orderNumber;
    }

    @PostMapping("/{orderNumber}/cancel")
    public String cancel(@PathVariable String orderNumber, RedirectAttributes redirectAttributes) {
        orderUseCase.cancelOrder(orderNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "受注「" + orderNumber + "」を取り消しました");
        return "redirect:/orders";
    }
}
```

</details>

### 16.2 動的フォーム（明細行の追加・削除）

htmx を使用して、ページをリロードせずに明細行を追加・削除できます。

<details>
<summary>コード例: 動的フォーム（明細行）</summary>

```html
<!-- 明細追加ボタン -->
<button type="button" class="btn btn-sm btn-outline-primary"
        hx-get="/orders/add-detail-row"
        hx-target="#detail-rows"
        hx-swap="beforeend"
        hx-vals='js:{"index": document.querySelectorAll("#detail-rows tr").length}'>
    <i class="bi bi-plus"></i> 行追加
</button>

<!-- 明細行フラグメント -->
<tr th:fragment="detailRow" th:id="'row-' + ${index}">
    <td>
        <select class="form-select product-select"
                th:name="'details[' + ${index} + '].productCode'" required
                onchange="updatePrice(this)">
            <option value="">選択</option>
            <option th:each="product : ${products}"
                    th:value="${product.productCode}"
                    th:text="${product.productCode + ' - ' + product.productName}"
                    th:data-price="${product.unitPrice}"></option>
        </select>
    </td>
    <td>
        <input type="number" class="form-control quantity-input"
               th:name="'details[' + ${index} + '].quantity'" min="1" value="1" required
               onchange="calculateAmount(this)">
    </td>
    <td>
        <input type="number" class="form-control price-input"
               th:name="'details[' + ${index} + '].unitPrice'" step="1" min="0"
               onchange="calculateAmount(this)">
    </td>
    <td>
        <input type="text" class="form-control amount-display" readonly value="0">
    </td>
    <td>
        <button type="button" class="btn btn-sm btn-outline-danger"
                onclick="this.closest('tr').remove(); calculateTotal()">削除</button>
    </td>
</tr>
```

</details>

### 16.3 出荷業務画面の実装

<details>
<summary>コード例: ShipmentController.java</summary>

```java
@Controller
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentUseCase shipmentUseCase;
    private final OrderUseCase orderUseCase;

    @GetMapping
    public String list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shipmentDate,
            Model model) {

        List<Shipment> shipments = shipmentDate != null
            ? shipmentUseCase.getShipmentsByDate(shipmentDate)
            : shipmentUseCase.getShipments(status);

        model.addAttribute("shipments", shipments);
        model.addAttribute("today", LocalDate.now());
        return "shipments/list";
    }

    @PostMapping("/{shipmentNumber}/execute")
    public String execute(@PathVariable String shipmentNumber, RedirectAttributes redirectAttributes) {
        try {
            shipmentUseCase.executeShipment(shipmentNumber);
            redirectAttributes.addFlashAttribute("successMessage",
                "出荷「" + shipmentNumber + "」を実行しました");
        } catch (InsufficientInventoryException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "在庫不足のため出荷できません: " + e.getMessage());
        }
        return "redirect:/shipments/" + shipmentNumber;
    }
}
```

</details>

---

## 第17章：エラーハンドリングと帳票出力

### 17.1 グローバル例外ハンドラ

<details>
<summary>コード例: GlobalExceptionHandler.java</summary>

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 業務エラー（リソース未検出）
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        logger.warn("リソース未検出: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    /**
     * 業務エラー（バリデーション）
     */
    @ExceptionHandler(BusinessValidationException.class)
    public String handleBusinessValidation(BusinessValidationException ex, Model model) {
        logger.warn("業務バリデーションエラー: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", ex.getErrorCode());
        return "error/business";
    }

    /**
     * システムエラー
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        logger.error("システムエラー", ex);
        model.addAttribute("errorMessage", "システムエラーが発生しました。管理者にお問い合わせください。");
        return "error/500";
    }
}
```

</details>

### 17.2 Excel 帳票出力

<details>
<summary>コード例: ReportController.java（Excel 出力）</summary>

```java
@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportUseCase reportUseCase;

    @GetMapping("/inventory/excel")
    public void exportInventoryExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=inventory_" + LocalDate.now() + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("在庫一覧");

            // ヘッダー行
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            String[] headers = {"商品コード", "商品名", "現在在庫", "安全在庫", "過不足"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // データ行
            List<InventoryData> inventoryList = reportUseCase.getInventoryReport();
            int rowNum = 1;
            for (InventoryData data : inventoryList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getProductCode());
                row.createCell(1).setCellValue(data.getProductName());
                row.createCell(2).setCellValue(data.getCurrentStock());
                row.createCell(3).setCellValue(data.getSafetyStock());
                row.createCell(4).setCellValue(data.getDifference());
            }

            // 列幅自動調整
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}
```

</details>

### 17.3 PDF 帳票出力

<details>
<summary>コード例: PDF 請求書出力</summary>

```java
@GetMapping("/invoice/{invoiceNumber}/pdf")
public void exportInvoicePdf(@PathVariable String invoiceNumber, HttpServletResponse response)
        throws IOException {

    Invoice invoice = invoiceUseCase.getInvoice(invoiceNumber);

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition",
        "attachment; filename=invoice_" + invoiceNumber + ".pdf");

    try (PdfWriter writer = new PdfWriter(response.getOutputStream());
         PdfDocument pdf = new PdfDocument(writer);
         Document document = new Document(pdf, PageSize.A4)) {

        // 日本語フォント設定
        PdfFont font = PdfFontFactory.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H");
        document.setFont(font);

        // タイトル
        document.add(new Paragraph("請求書")
            .setFontSize(24)
            .setTextAlignment(TextAlignment.CENTER));

        // 請求先情報
        document.add(new Paragraph(invoice.getCustomerName() + " 御中")
            .setFontSize(14));

        // 請求明細テーブル
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 5, 2, 2, 2}))
            .useAllAvailableWidth();

        // ヘッダー
        table.addHeaderCell(new Cell().add(new Paragraph("商品コード")));
        table.addHeaderCell(new Cell().add(new Paragraph("商品名")));
        table.addHeaderCell(new Cell().add(new Paragraph("数量")));
        table.addHeaderCell(new Cell().add(new Paragraph("単価")));
        table.addHeaderCell(new Cell().add(new Paragraph("金額")));

        // 明細行
        for (InvoiceDetail detail : invoice.getDetails()) {
            table.addCell(detail.getProductCode());
            table.addCell(detail.getProductName());
            table.addCell(String.valueOf(detail.getQuantity()));
            table.addCell(formatCurrency(detail.getUnitPrice()));
            table.addCell(formatCurrency(detail.getAmount()));
        }

        document.add(table);

        // 合計
        document.add(new Paragraph("合計金額: " + formatCurrency(invoice.getTotalAmount()))
            .setTextAlignment(TextAlignment.RIGHT)
            .setFontSize(16)
            .setBold());
    }
}
```

</details>

---

## 第10部-B のまとめ

### API サーバー版との比較

| 観点 | API サーバー版 | モノリス版 |
|------|--------------|-----------|
| **アーキテクチャ** | フロントエンド分離（SPA） | 統合（Thymeleaf） |
| **通信** | REST API（JSON） | サーバーサイドレンダリング（HTML） |
| **状態管理** | クライアント側（localStorage 等） | サーバー側（セッション） |
| **部分更新** | JavaScript（Axios 等） | htmx |
| **帳票出力** | API 経由でダウンロード | 直接ダウンロード |
| **認証** | JWT/OAuth | セッションベース |
| **複雑さ** | 高（API 設計、CORS 等） | 低（シンプル） |
| **開発速度** | 初期は遅い | 初期は速い |

### 実装した機能

| カテゴリ | 機能 |
|---------|------|
| **商品マスタ** | 一覧、詳細、登録、編集、削除、検索 |
| **取引先マスタ** | 一覧、詳細（取引履歴）、登録、編集、削除、検索 |
| **受注** | 一覧、登録（動的明細）、確定、取消 |
| **出荷** | 一覧、出荷指示、出荷実行、取消 |
| **請求** | 一覧、締処理、請求書発行 |
| **入金** | 一覧、入金登録、消込処理 |
| **帳票** | Excel/PDF 出力 |
| **共通** | レイアウト、エラーハンドリング、フラッシュメッセージ |

### 技術スタック

| カテゴリ | 技術 |
|---------|------|
| **言語** | Java 21 |
| **フレームワーク** | Spring Boot 3.4 |
| **テンプレート** | Thymeleaf |
| **部分更新** | htmx |
| **CSS** | Bootstrap 5 |
| **ORM** | MyBatis |
| **データベース** | PostgreSQL 16 |
| **Excel** | Apache POI |
| **PDF** | iText7 |
| **テスト** | JUnit 5, TestContainers |

### モノリスを選択すべき場面

1. **社内システム**: 限られたユーザー、SEO 不要
2. **業務アプリケーション**: 複雑な業務フロー
3. **小規模チーム**: フルスタック開発
4. **迅速な開発**: MVP、プロトタイプ
5. **運用コスト重視**: シンプルなインフラ

モノリスアーキテクチャは決して「古い」アーキテクチャではありません。適切な場面で選択することで、シンプルかつ効率的なシステム開発が可能です。
