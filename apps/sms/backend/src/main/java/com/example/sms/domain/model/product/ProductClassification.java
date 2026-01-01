package com.example.sms.domain.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品分類エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.RedundantFieldInitializer")
public class ProductClassification {
    private String classificationCode;
    private String classificationName;
    @Builder.Default
    private Integer hierarchyLevel = 0;
    private String classificationPath;
    @Builder.Default
    private boolean isLeaf = false;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
