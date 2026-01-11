package com.example.pms.application.port.out;

import com.example.pms.domain.model.plan.Requirement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 所要情報リポジトリ（Output Port）
 * ドメイン層がデータアクセスに依存しないためのインターフェース
 */
public interface RequirementRepository {

    /**
     * 所要情報を保存する
     */
    void save(Requirement requirement);

    /**
     * IDで所要情報を検索する
     */
    Optional<Requirement> findById(Integer id);

    /**
     * 所要番号で所要情報を検索する
     */
    Optional<Requirement> findByRequirementNumber(String requirementNumber);

    /**
     * オーダIDで所要情報を検索する
     */
    List<Requirement> findByOrderId(Integer orderId);

    /**
     * すべての所要情報を取得する
     */
    List<Requirement> findAll();

    /**
     * 引当情報を更新する
     */
    void updateAllocation(Integer id, BigDecimal allocatedQuantity, BigDecimal shortageQuantity);

    /**
     * すべての所要情報を削除する
     */
    void deleteAll();
}
