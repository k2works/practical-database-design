package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ProductClassificationRepository;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.product.ProductClassification;
import com.example.sms.infrastructure.out.persistence.mapper.ProductClassificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商品分類リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class ProductClassificationRepositoryImpl implements ProductClassificationRepository {

    private final ProductClassificationMapper productClassificationMapper;

    @Override
    public void save(ProductClassification classification) {
        productClassificationMapper.insert(classification);
    }

    @Override
    public Optional<ProductClassification> findByCode(String classificationCode) {
        return productClassificationMapper.findByCode(classificationCode);
    }

    @Override
    public List<ProductClassification> findAll() {
        return productClassificationMapper.findAll();
    }

    @Override
    public List<ProductClassification> findChildren(String parentPath) {
        return productClassificationMapper.findByPathPrefix(parentPath);
    }

    @Override
    public PageResult<ProductClassification> findWithPagination(int page, int size, String keyword) {
        int offset = page * size;
        List<ProductClassification> classifications = productClassificationMapper.findWithPagination(offset, size, keyword);
        long totalElements = productClassificationMapper.count(keyword);
        return new PageResult<>(classifications, page, size, totalElements);
    }

    @Override
    public void update(ProductClassification classification) {
        productClassificationMapper.update(classification);
    }

    @Override
    public void deleteAll() {
        productClassificationMapper.deleteAll();
    }
}
