package com.example.sms.application.port.in;

import com.example.sms.domain.model.product.ProductClassification;

import java.util.List;

/**
 * 商品分類ユースケース（Input Port）.
 */
public interface ProductClassificationUseCase {

    /**
     * 全商品分類を取得する.
     *
     * @return 商品分類リスト
     */
    List<ProductClassification> getAllClassifications();

    /**
     * 分類コードで商品分類を取得する.
     *
     * @param classificationCode 分類コード
     * @return 商品分類
     */
    ProductClassification getClassificationByCode(String classificationCode);

    /**
     * 子分類を検索する.
     *
     * @param parentPath 親パス
     * @return 商品分類リスト
     */
    List<ProductClassification> getChildClassifications(String parentPath);

    /**
     * 商品分類を登録する.
     *
     * @param classification 商品分類
     */
    void createClassification(ProductClassification classification);

    /**
     * 商品分類を更新する.
     *
     * @param classification 商品分類
     */
    void updateClassification(ProductClassification classification);
}
