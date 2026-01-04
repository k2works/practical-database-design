package com.example.sms.application.service;

import com.example.sms.application.port.in.ProductClassificationUseCase;
import com.example.sms.application.port.out.ProductClassificationRepository;
import com.example.sms.domain.exception.ProductClassificationNotFoundException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.product.ProductClassification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品分類アプリケーションサービス.
 */
@Service
@Transactional
public class ProductClassificationService implements ProductClassificationUseCase {

    private final ProductClassificationRepository classificationRepository;

    public ProductClassificationService(ProductClassificationRepository classificationRepository) {
        this.classificationRepository = classificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductClassification> getAllClassifications() {
        return classificationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ProductClassification> getClassifications(int page, int size, String keyword) {
        return classificationRepository.findWithPagination(page, size, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductClassification getClassificationByCode(String classificationCode) {
        return classificationRepository.findByCode(classificationCode)
            .orElseThrow(() -> new ProductClassificationNotFoundException(classificationCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductClassification> getChildClassifications(String parentPath) {
        return classificationRepository.findChildren(parentPath);
    }

    @Override
    public void createClassification(ProductClassification classification) {
        classificationRepository.save(classification);
    }

    @Override
    public void updateClassification(ProductClassification classification) {
        classificationRepository.update(classification);
    }
}
