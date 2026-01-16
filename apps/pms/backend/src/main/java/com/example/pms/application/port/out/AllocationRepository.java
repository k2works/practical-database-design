package com.example.pms.application.port.out;

import com.example.pms.domain.model.plan.Allocation;

import java.util.List;
import java.util.Optional;

/**
 * 引当情報リポジトリ（Output Port）
 * ドメイン層がデータアクセスに依存しないためのインターフェース
 */
public interface AllocationRepository {

    /**
     * 引当情報を保存する
     */
    void save(Allocation allocation);

    /**
     * IDで引当情報を検索する
     */
    Optional<Allocation> findById(Integer id);

    /**
     * 所要IDで引当情報を検索する
     */
    List<Allocation> findByRequirementId(Integer requirementId);

    /**
     * すべての引当情報を取得する
     */
    List<Allocation> findAll();

    /**
     * すべての引当情報を削除する
     */
    void deleteAll();
}
