package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.product.ProductCategory;
import com.example.sms.infrastructure.out.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商品リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;

    @Override
    public void save(Product product) {
        productMapper.insert(product);
    }

    @Override
    public Optional<Product> findByCode(String productCode) {
        return productMapper.findByCode(productCode);
    }

    @Override
    public List<Product> findAll() {
        return productMapper.findAll();
    }

    @Override
    public List<Product> findByCategory(ProductCategory category) {
        return productMapper.findByCategory(category.getDisplayName());
    }

    @Override
    public List<Product> findByClassificationCode(String classificationCode) {
        return productMapper.findByClassificationCode(classificationCode);
    }

    @Override
    public PageResult<Product> findWithPagination(int page, int size, ProductCategory category, String keyword) {
        int offset = page * size;
        String categoryName = category != null ? category.getDisplayName() : null;

        List<Product> products = productMapper.findWithPagination(offset, size, categoryName, keyword);
        long totalElements = productMapper.count(categoryName, keyword);

        return new PageResult<>(products, page, size, totalElements);
    }

    @Override
    public void update(Product product) {
        productMapper.update(product);
    }

    @Override
    public void deleteByCode(String productCode) {
        productMapper.deleteByCode(productCode);
    }

    @Override
    public void deleteAll() {
        productMapper.deleteAll();
    }
}
