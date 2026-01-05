package com.example.sms.infrastructure.in.rest.controller;

import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.command.CreateProductCommand;
import com.example.sms.application.port.in.command.UpdateProductCommand;
import com.example.sms.domain.model.product.Product;
import com.example.sms.infrastructure.in.rest.dto.CreateProductRequest;
import com.example.sms.infrastructure.in.rest.dto.ProductResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品マスタ API コントローラー.
 */
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "products", description = "商品マスタ API")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    @Operation(summary = "商品一覧の取得", description = "すべての商品を取得します")
    @ApiResponse(responseCode = "200", description = "商品一覧を返却")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productUseCase.getAllProducts();
        List<ProductResponse> responses = products.stream()
            .map(ProductResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{productCode}")
    @Operation(summary = "商品の取得", description = "商品コードを指定して商品情報を取得します")
    @ApiResponse(responseCode = "200", description = "商品を返却")
    @ApiResponse(responseCode = "404", description = "商品が見つからない")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "商品コード", example = "BEEF-001")
            @PathVariable String productCode) {

        Product product = productUseCase.getProductByCode(productCode);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @PostMapping
    @Operation(summary = "商品の登録", description = "新規商品を登録します")
    @ApiResponse(responseCode = "201", description = "商品を登録")
    @ApiResponse(responseCode = "409", description = "商品が既に存在する")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        CreateProductCommand command = new CreateProductCommand(
            request.productCode(),
            request.productFullName(),
            request.productName(),
            request.productNameKana(),
            request.productCategory(),
            request.modelNumber(),
            request.sellingPrice(),
            request.purchasePrice(),
            request.taxCategory(),
            request.classificationCode(),
            request.isMiscellaneous(),
            request.isInventoryManaged(),
            request.isInventoryAllocated(),
            request.supplierCode(),
            request.supplierBranchNumber()
        );

        Product product = productUseCase.createProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ProductResponse.from(product));
    }

    @PutMapping("/{productCode}")
    @Operation(summary = "商品の更新", description = "商品コードを指定して商品情報を更新します")
    @ApiResponse(responseCode = "200", description = "商品を更新")
    @ApiResponse(responseCode = "404", description = "商品が見つからない")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "商品コード", example = "BEEF-001")
            @PathVariable String productCode,
            @Valid @RequestBody UpdateProductRequest request) {

        UpdateProductCommand command = new UpdateProductCommand(
            request.productFullName(),
            request.productName(),
            request.productNameKana(),
            request.productCategory(),
            request.modelNumber(),
            request.sellingPrice(),
            request.purchasePrice(),
            request.taxCategory(),
            request.classificationCode(),
            request.isMiscellaneous(),
            request.isInventoryManaged(),
            request.isInventoryAllocated(),
            request.supplierCode(),
            request.supplierBranchNumber()
        );

        Product product = productUseCase.updateProduct(productCode, command);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @DeleteMapping("/{productCode}")
    @Operation(summary = "商品の削除", description = "商品コードを指定して商品を削除します")
    @ApiResponse(responseCode = "204", description = "商品を削除")
    @ApiResponse(responseCode = "404", description = "商品が見つからない")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "商品コード", example = "BEEF-001")
            @PathVariable String productCode) {

        productUseCase.deleteProduct(productCode);
        return ResponseEntity.noContent().build();
    }
}
