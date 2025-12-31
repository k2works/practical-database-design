# 第13章：API サービスの実装

本章では、販売管理システムのデータベース設計を外部から利用できるようにするため、RESTful API サービスを実装します。ヘキサゴナルアーキテクチャ（Ports and Adapters）を採用し、ドメインロジックを外部技術から分離した保守性の高い API を構築します。

---

## 13.1 ヘキサゴナルアーキテクチャの復習

### Ports and Adapters パターンの概要

ヘキサゴナルアーキテクチャ（Ports and Adapters パターン）は、Alistair Cockburn によって提唱された設計手法で、アプリケーションの中核となるビジネスロジック（ドメイン層）を外部の技術的詳細から完全に分離することを目的とします。

```plantuml
@startuml hexagonal_architecture_sales
!define RECTANGLE class

package "Hexagonal Architecture (販売管理API)" {

  RECTANGLE "Application Core\n(Domain + Use Cases)" as core {
    - Product (商品)
    - Partner (取引先)
    - Order (受注)
    - Sales (売上)
    - Invoice (請求)
    - ProductUseCase
    - OrderUseCase
    - SalesUseCase
  }

  RECTANGLE "Input Adapters\n(Driving Side)" as input {
    - Spring Controllers
    - REST API Endpoints
    - Request Validation
    - Error Handling
  }

  RECTANGLE "Output Adapters\n(Driven Side)" as output {
    - MyBatis Repository
    - Database Access
    - Entity Mapping
  }
}

input --> core : "Input Ports\n(Use Cases)"
core --> output : "Output Ports\n(Repository Interfaces)"

note top of core
  純粋なビジネスロジック
  MyBatis に依存しない
  高速でテスト可能
end note

note left of input
  外部からアプリケーションを
  駆動するアダプター
  HTTP, REST等
end note

note right of output
  アプリケーションが外部の
  技術を使うためのアダプター
  PostgreSQL, MyBatis等
end note
@enduml
```

### ドメイン中心設計

ビジネスロジックを中心に据え、外部技術（DB、Web、UI など）を周辺に配置します。これにより、ビジネスルールが技術的な関心事から独立し、変更に強い設計が実現できます。

### 依存性の逆転

ドメイン層は外部に依存せず、外部がドメイン層に依存します。具体的には、リポジトリのインターフェース（Output Port）をドメイン層で定義し、その実装（Adapter）をインフラストラクチャ層に配置します。

### テスト容易性

モックやスタブを使った単体テストが容易になります。ドメインロジックを独立してテストでき、外部依存（データベースなど）なしで高速なテストが可能です。

---

## 13.2 アーキテクチャ構造

### レイヤー構成

販売管理 API の実装では、以下のレイヤー構造を採用します。

```
src/main/java/com/example/sales/
├── domain/                     # ドメイン層（純粋なビジネスロジック）
│   ├── model/                 # ドメインモデル（エンティティ、値オブジェクト）
│   │   ├── master/            # マスタ関連
│   │   ├── sales/             # 販売関連
│   │   ├── purchase/          # 仕入関連
│   │   ├── inventory/         # 在庫関連
│   │   └── billing/           # 請求関連
│   └── exception/             # ドメイン例外
│
├── application/               # アプリケーション層
│   ├── port/
│   │   ├── in/               # Input Port（ユースケースインターフェース）
│   │   └── out/              # Output Port（リポジトリインターフェース）
│   └── service/              # Application Service（ユースケース実装）
│
├── infrastructure/            # インフラストラクチャ層
│   ├── datasource/           # Output Adapter（DB実装）
│   │   ├── mapper/           # MyBatis Mapper
│   │   └── repository/       # Repository実装
│   └── rest/                 # Input Adapter（Web実装）
│       ├── controller/       # REST Controller（Spring MVC）
│       ├── dto/              # Data Transfer Object
│       └── exception/        # Exception Handler
│
└── config/                   # 設定クラス
```

### Domain 層

ビジネスルールとドメインモデルを定義します。外部技術に依存しない純粋な Java コードで構成されます。

<details>
<summary>Product.java（商品ドメインモデル）</summary>

```java
package com.example.sales.domain.model.master;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商品ドメインモデル
 */
public record Product(
    String productCode,
    LocalDate effectiveDate,
    String productName,
    String categoryCode,
    LocalDate categoryEffectiveDate,
    ProductType productType,
    TaxType taxType,
    BigDecimal sellingPrice,
    BigDecimal purchasePrice
) {}
```

</details>

### Application 層

ユースケースの実装とオーケストレーションを担当します。

<details>
<summary>ProductUseCase.java（Input Port）</summary>

```java
package com.example.sales.application.port.in;

/**
 * 商品ユースケース（Input Port）
 */
public interface ProductUseCase {
    Product createProduct(CreateProductCommand command);
    Product updateProduct(UpdateProductCommand command);
    List<Product> getAllProducts();
    Product getProductByCode(String productCode);
    void deleteProduct(String productCode);
}
```

</details>

### Infrastructure 層

外部技術との接続を担当します。DB アクセス（MyBatis）や Web フレームワーク（Spring MVC）の実装を含みます。

### Input Port / Output Port の分離

| ポート | 役割 | 例 |
|--------|------|-----|
| Input Port | アプリケーションへの入力インターフェース | `ProductUseCase`, `OrderUseCase` |
| Output Port | アプリケーションからの出力インターフェース | `ProductRepository`, `OrderRepository` |

---

## 13.3 マスタ API の実装

### 商品マスタ API（CRUD エンドポイント）

#### Output Port（リポジトリインターフェース）

<details>
<summary>ProductRepository.java</summary>

```java
package com.example.sales.application.port.out;

/**
 * 商品リポジトリ（Output Port）
 */
public interface ProductRepository {
    Product save(Product product);
    List<Product> findAll();
    Optional<Product> findByCode(String productCode, LocalDate effectiveDate);
    List<Product> findByType(ProductType type);
    List<Product> findByCategoryCode(String categoryCode);
    void deleteByCode(String productCode, LocalDate effectiveDate);
}
```

</details>

#### Input Port（ユースケースインターフェース）

<details>
<summary>コマンドオブジェクト</summary>

```java
package com.example.sales.application.port.in;

public record CreateProductCommand(
    String productCode,
    String productName,
    String categoryCode,
    ProductType productType,
    TaxType taxType,
    BigDecimal sellingPrice,
    BigDecimal purchasePrice
) {}

public record UpdateProductCommand(
    String productCode,
    String productName,
    ProductType productType,
    TaxType taxType,
    BigDecimal sellingPrice,
    BigDecimal purchasePrice
) {}
```

</details>

### TDD による実装（Red-Green-Refactor）

#### Red: 失敗するテストを書く

<details>
<summary>ProductControllerTest.java</summary>

```java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("商品マスタ API テスト")
class ProductControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("sales_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("POST /api/v1/products")
    class CreateProduct {

        @Test
        @DisplayName("商品を登録できる")
        void shouldCreateProduct() throws Exception {
            var request = """
                {
                    "productCode": "BEEF-TEST",
                    "productName": "テスト商品",
                    "categoryCode": "CAT-BEEF",
                    "productType": "PRODUCT",
                    "taxType": "STANDARD",
                    "sellingPrice": 5000,
                    "purchasePrice": 3000
                }
                """;

            mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productCode").value("BEEF-TEST"))
                .andExpect(jsonPath("$.productName").value("テスト商品"));
        }
    }
}
```

</details>

#### Green: テストを通す実装

<details>
<summary>ProductController.java</summary>

```java
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "products", description = "商品マスタ API")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @PostMapping
    @Operation(summary = "商品の登録")
    @ApiResponse(responseCode = "201", description = "商品を登録")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        CreateProductCommand command = new CreateProductCommand(
            request.productCode(),
            request.productName(),
            request.categoryCode(),
            request.productType(),
            request.taxType(),
            request.sellingPrice(),
            request.purchasePrice()
        );

        Product product = productUseCase.createProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProductResponse.from(product));
    }
}
```

</details>

### Controller・Service・Repository の実装

#### Application Service

<details>
<summary>ProductService.java</summary>

```java
@Service
@Transactional
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(CreateProductCommand command) {
        LocalDate effectiveDate = LocalDate.now();

        // 重複チェック
        productRepository.findByCode(command.productCode(), effectiveDate)
            .ifPresent(existing -> {
                throw new DuplicateProductException(command.productCode());
            });

        Product product = new Product(
            command.productCode(),
            effectiveDate,
            command.productName(),
            command.categoryCode(),
            effectiveDate,
            command.productType(),
            command.taxType(),
            command.sellingPrice(),
            command.purchasePrice()
        );

        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductByCode(String productCode) {
        LocalDate effectiveDate = LocalDate.now();
        return productRepository.findByCode(productCode, effectiveDate)
            .orElseThrow(() -> new ProductNotFoundException(productCode));
    }
}
```

</details>

### 顧客マスタ API（請求先・回収先の管理）

顧客マスタ API では、請求先と回収先の概念を正しく管理します。

<details>
<summary>CustomerController.java</summary>

```java
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "customers", description = "顧客 API")
public class CustomerController {

    private final CustomerUseCase customerUseCase;

    @GetMapping("/{customerCode}")
    @Operation(summary = "顧客の取得")
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable String customerCode) {

        Customer customer = customerUseCase.getCustomerByCode(customerCode);
        return ResponseEntity.ok(CustomerResponse.from(customer));
    }

    @GetMapping("/{customerCode}/billing-destinations")
    @Operation(summary = "請求先一覧の取得")
    public ResponseEntity<List<BillingDestinationResponse>> getBillingDestinations(
            @PathVariable String customerCode) {

        List<BillingDestination> destinations =
            customerUseCase.getBillingDestinations(customerCode);
        return ResponseEntity.ok(destinations.stream()
            .map(BillingDestinationResponse::from)
            .toList());
    }
}
```

</details>

---

## 13.4 トランザクション API の実装

### 受注 API（受注登録・受注照会・受注明細照会）

<details>
<summary>OrderController.java</summary>

```java
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "orders", description = "受注 API")
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    @Operation(summary = "受注の登録")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        Order order = orderUseCase.createOrder(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(OrderResponse.from(order));
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "受注の取得")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable String orderNumber) {

        Order order = orderUseCase.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/{orderNumber}/details")
    @Operation(summary = "受注明細の取得")
    public ResponseEntity<List<OrderDetailResponse>> getOrderDetails(
            @PathVariable String orderNumber) {

        List<OrderDetail> details = orderUseCase.getOrderDetails(orderNumber);
        return ResponseEntity.ok(details.stream()
            .map(OrderDetailResponse::from)
            .toList());
    }
}
```

</details>

### 出荷 API（出荷指示・出荷確定）

<details>
<summary>ShipmentController.java</summary>

```java
@RestController
@RequestMapping("/api/v1/shipments")
@Tag(name = "shipments", description = "出荷 API")
public class ShipmentController {

    private final ShipmentUseCase shipmentUseCase;

    @PostMapping("/instructions")
    @Operation(summary = "出荷指示の登録")
    public ResponseEntity<ShipmentInstructionResponse> createShipmentInstruction(
            @Valid @RequestBody CreateShipmentInstructionRequest request) {

        ShipmentInstruction instruction =
            shipmentUseCase.createInstruction(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ShipmentInstructionResponse.from(instruction));
    }

    @PostMapping("/{shipmentNumber}/confirm")
    @Operation(summary = "出荷確定")
    public ResponseEntity<ShipmentResponse> confirmShipment(
            @PathVariable String shipmentNumber) {

        Shipment shipment = shipmentUseCase.confirmShipment(shipmentNumber);
        return ResponseEntity.ok(ShipmentResponse.from(shipment));
    }
}
```

</details>

### 売上 API（売上計上・売上照会）

<details>
<summary>SalesController.java</summary>

```java
@RestController
@RequestMapping("/api/v1/sales")
@Tag(name = "sales", description = "売上 API")
public class SalesController {

    private final SalesUseCase salesUseCase;

    @PostMapping
    @Operation(summary = "売上計上")
    public ResponseEntity<SalesResponse> recordSales(
            @Valid @RequestBody RecordSalesRequest request) {

        Sales sales = salesUseCase.recordSales(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SalesResponse.from(sales));
    }

    @GetMapping
    @Operation(summary = "売上一覧の取得")
    public ResponseEntity<List<SalesResponse>> getSalesList(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String customerCode) {

        List<Sales> salesList = salesUseCase.getSalesList(fromDate, toDate, customerCode);
        return ResponseEntity.ok(salesList.stream()
            .map(SalesResponse::from)
            .toList());
    }
}
```

</details>

### 請求・入金 API（請求締め・入金消込）

<details>
<summary>InvoiceController.java / ReceiptController.java</summary>

```java
@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "invoices", description = "請求 API")
public class InvoiceController {

    private final InvoiceUseCase invoiceUseCase;

    @PostMapping("/closing")
    @Operation(summary = "請求締め処理")
    public ResponseEntity<List<InvoiceResponse>> executeClosing(
            @Valid @RequestBody InvoiceClosingRequest request) {

        List<Invoice> invoices = invoiceUseCase.executeClosing(
            request.closingDate(),
            request.customerCode()
        );
        return ResponseEntity.ok(invoices.stream()
            .map(InvoiceResponse::from)
            .toList());
    }
}

@RestController
@RequestMapping("/api/v1/receipts")
@Tag(name = "receipts", description = "入金 API")
public class ReceiptController {

    private final ReceiptUseCase receiptUseCase;

    @PostMapping
    @Operation(summary = "入金登録")
    public ResponseEntity<ReceiptResponse> recordReceipt(
            @Valid @RequestBody RecordReceiptRequest request) {

        Receipt receipt = receiptUseCase.recordReceipt(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ReceiptResponse.from(receipt));
    }

    @PostMapping("/{receiptNumber}/apply")
    @Operation(summary = "入金消込")
    public ResponseEntity<ReceiptResponse> applyReceipt(
            @PathVariable String receiptNumber,
            @Valid @RequestBody ApplyReceiptRequest request) {

        Receipt receipt = receiptUseCase.applyToInvoices(
            receiptNumber,
            request.invoiceNumbers()
        );
        return ResponseEntity.ok(ReceiptResponse.from(receipt));
    }
}
```

</details>

---

## 13.5 エラーハンドリング

### グローバル例外ハンドラー（@RestControllerAdvice）

<details>
<summary>GlobalExceptionHandler.java</summary>

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());

        ErrorResponse response = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND",
            e.getMessage(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException e) {
        log.warn("Duplicate resource: {}", e.getMessage());

        ErrorResponse response = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "CONFLICT",
            e.getMessage(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(BusinessRuleViolationException e) {
        log.warn("Business rule violation: {}", e.getMessage());

        ErrorResponse response = new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "BUSINESS_RULE_VIOLATION",
            e.getMessage(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            "入力値が不正です",
            errors,
            Instant.now()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);

        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR",
            "システムエラーが発生しました",
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

</details>

### ドメイン例外の定義と変換

<details>
<summary>ドメイン例外クラス</summary>

```java
package com.example.sales.domain.exception;

public abstract class ResourceNotFoundException extends RuntimeException {
    protected ResourceNotFoundException(String message) {
        super(message);
    }
}

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException(String productCode) {
        super("商品が見つかりません: " + productCode);
    }
}

public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException(String orderNumber) {
        super("受注が見つかりません: " + orderNumber);
    }
}

public abstract class BusinessRuleViolationException extends RuntimeException {
    protected BusinessRuleViolationException(String message) {
        super(message);
    }
}

public class CreditLimitExceededException extends BusinessRuleViolationException {
    public CreditLimitExceededException(String customerCode, BigDecimal limit, BigDecimal amount) {
        super(String.format(
            "与信限度額を超過しています（顧客: %s, 限度額: %s, 申請額: %s）",
            customerCode, limit, amount
        ));
    }
}

public class InsufficientInventoryException extends BusinessRuleViolationException {
    public InsufficientInventoryException(String productCode, BigDecimal available, BigDecimal requested) {
        super(String.format(
            "在庫が不足しています（商品: %s, 有効在庫: %s, 要求数量: %s）",
            productCode, available, requested
        ));
    }
}
```

</details>

### ProblemDetail によるエラーレスポンス

RFC 7807 に準拠した ProblemDetail 形式でエラーを返却することも可能です。

<details>
<summary>エラーレスポンス DTO</summary>

```java
public record ErrorResponse(
    int status,
    String code,
    String message,
    Instant timestamp
) {}

public record ValidationErrorResponse(
    int status,
    String code,
    String message,
    Map<String, String> errors,
    Instant timestamp
) {}
```

</details>

### バリデーションエラーの処理

リクエスト DTO に Bean Validation アノテーションを付与し、バリデーションエラーを統一的に処理します。

<details>
<summary>CreateProductRequest.java</summary>

```java
public record CreateProductRequest(
    @NotBlank(message = "商品コードは必須です")
    String productCode,

    @NotBlank(message = "商品名は必須です")
    String productName,

    @NotBlank(message = "商品分類コードは必須です")
    String categoryCode,

    @NotNull(message = "商品区分は必須です")
    ProductType productType,

    @NotNull(message = "税区分は必須です")
    TaxType taxType,

    @NotNull(message = "販売単価は必須です")
    @Positive(message = "販売単価は正の数である必要があります")
    BigDecimal sellingPrice,

    @NotNull(message = "仕入単価は必須です")
    @Positive(message = "仕入単価は正の数である必要があります")
    BigDecimal purchasePrice
) {}
```

</details>

---

## 13.6 API ドキュメント

### OpenAPI / Swagger の設定

<details>
<summary>OpenApiConfig.java</summary>

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("販売管理システム API")
                .description("TDD で育てる販売管理システムの API ドキュメント")
                .version("1.0.0"))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("開発サーバー")))
            .tags(List.of(
                new Tag().name("products").description("商品マスタ API"),
                new Tag().name("partners").description("取引先マスタ API"),
                new Tag().name("customers").description("顧客 API"),
                new Tag().name("orders").description("受注 API"),
                new Tag().name("shipments").description("出荷 API"),
                new Tag().name("sales").description("売上 API"),
                new Tag().name("invoices").description("請求 API"),
                new Tag().name("receipts").description("入金 API")));
    }
}
```

</details>

### エンドポイントの文書化

<details>
<summary>アノテーションによるドキュメント化</summary>

```java
@GetMapping("/{productCode}")
@Operation(
    summary = "商品の取得",
    description = "商品コードを指定して商品情報を取得します"
)
@ApiResponse(responseCode = "200", description = "商品を返却")
@ApiResponse(responseCode = "404", description = "商品が見つからない")
public ResponseEntity<ProductResponse> getProduct(
        @Parameter(description = "商品コード", example = "BEEF-001")
        @PathVariable String productCode) {
    // ...
}
```

</details>

### リクエスト・レスポンスのスキーマ定義

application.yml に Swagger UI の設定を追加します。

<details>
<summary>application.yml（Swagger 設定）</summary>

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  api-docs:
    path: /api-docs
```

</details>

Swagger UI にアクセスすると、API ドキュメントが自動生成されます。

---

## 本章のまとめ

本章では、販売管理システムの API サービスを実装しました。

### 実装したコンポーネント

| カテゴリ | 内容 |
|----------|------|
| アーキテクチャ | ヘキサゴナルアーキテクチャ（Ports & Adapters） |
| マスタ API | 商品マスタ、顧客マスタ |
| トランザクション API | 受注、出荷、売上、請求・入金 |
| エラーハンドリング | グローバル例外ハンドラー、ドメイン例外 |
| API ドキュメント | OpenAPI / Swagger UI |

### アーキテクチャの利点

1. **テスト容易性**: ドメインロジックを独立してテスト可能
2. **技術変更の容易さ**: アダプターを差し替えるだけで技術を変更可能
3. **保守性**: 関心事の分離による高い保守性
4. **チーム開発**: レイヤーごとに並行開発が可能

### エンドポイント一覧

| メソッド | パス | 説明 |
|----------|------|------|
| GET | `/api/v1/products` | 商品一覧の取得 |
| GET | `/api/v1/products/{code}` | 商品の取得 |
| POST | `/api/v1/products` | 商品の登録 |
| PUT | `/api/v1/products/{code}` | 商品の更新 |
| DELETE | `/api/v1/products/{code}` | 商品の削除 |
| GET | `/api/v1/orders` | 受注一覧の取得 |
| POST | `/api/v1/orders` | 受注の登録 |
| POST | `/api/v1/shipments/instructions` | 出荷指示の登録 |
| POST | `/api/v1/shipments/{number}/confirm` | 出荷確定 |
| POST | `/api/v1/sales` | 売上計上 |
| POST | `/api/v1/invoices/closing` | 請求締め処理 |
| POST | `/api/v1/receipts` | 入金登録 |
| POST | `/api/v1/receipts/{number}/apply` | 入金消込 |
