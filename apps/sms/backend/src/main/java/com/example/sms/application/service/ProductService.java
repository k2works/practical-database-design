package com.example.sms.application.service;

import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.command.CreateProductCommand;
import com.example.sms.application.port.in.command.UpdateProductCommand;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.domain.exception.DuplicateProductException;
import com.example.sms.domain.exception.ProductNotFoundException;
import com.example.sms.domain.model.product.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品アプリケーションサービス.
 */
@Service
@Transactional
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(CreateProductCommand command) {
        // 重複チェック
        productRepository.findByCode(command.productCode())
            .ifPresent(existing -> {
                throw new DuplicateProductException(command.productCode());
            });

        Product product = Product.builder()
            .productCode(command.productCode())
            .productFullName(command.productFullName())
            .productName(command.productName())
            .productNameKana(command.productNameKana())
            .productCategory(command.productCategory())
            .modelNumber(command.modelNumber())
            .sellingPrice(command.sellingPrice())
            .purchasePrice(command.purchasePrice())
            .taxCategory(command.taxCategory())
            .classificationCode(command.classificationCode())
            .isMiscellaneous(command.isMiscellaneous() != null && command.isMiscellaneous())
            .isInventoryManaged(command.isInventoryManaged() == null || command.isInventoryManaged())
            .isInventoryAllocated(command.isInventoryAllocated() == null || command.isInventoryAllocated())
            .supplierCode(command.supplierCode())
            .supplierBranchNumber(command.supplierBranchNumber())
            .build();

        productRepository.save(product);
        return product;
    }

    @Override
    public Product updateProduct(String productCode, UpdateProductCommand command) {
        Product existing = productRepository.findByCode(productCode)
            .orElseThrow(() -> new ProductNotFoundException(productCode));

        Product updated = Product.builder()
            .productCode(productCode)
            .productFullName(command.productFullName())
            .productName(command.productName())
            .productNameKana(command.productNameKana())
            .productCategory(command.productCategory())
            .modelNumber(command.modelNumber())
            .sellingPrice(command.sellingPrice())
            .purchasePrice(command.purchasePrice())
            .taxCategory(command.taxCategory())
            .classificationCode(command.classificationCode())
            .isMiscellaneous(command.isMiscellaneous() != null && command.isMiscellaneous())
            .isInventoryManaged(command.isInventoryManaged() == null || command.isInventoryManaged())
            .isInventoryAllocated(command.isInventoryAllocated() == null || command.isInventoryAllocated())
            .supplierCode(command.supplierCode())
            .supplierBranchNumber(command.supplierBranchNumber())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .build();

        productRepository.update(updated);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductByCode(String productCode) {
        return productRepository.findByCode(productCode)
            .orElseThrow(() -> new ProductNotFoundException(productCode));
    }

    @Override
    public void deleteProduct(String productCode) {
        productRepository.findByCode(productCode)
            .orElseThrow(() -> new ProductNotFoundException(productCode));

        productRepository.deleteByCode(productCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByClassification(String classificationCode) {
        return productRepository.findByClassificationCode(classificationCode);
    }
}
