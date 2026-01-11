package com.example.pms.application.port.out;

import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.PlanStatus;

import java.util.List;
import java.util.Optional;

/**
 * 基準生産計画リポジトリ（Output Port）
 * ドメイン層がデータアクセスに依存しないためのインターフェース
 */
public interface MpsRepository {

    /**
     * 基準生産計画を保存する
     */
    void save(MasterProductionSchedule mps);

    /**
     * IDで基準生産計画を検索する
     */
    Optional<MasterProductionSchedule> findById(Integer id);

    /**
     * MPS番号で基準生産計画を検索する
     */
    Optional<MasterProductionSchedule> findByMpsNumber(String mpsNumber);

    /**
     * ステータスで基準生産計画を検索する
     */
    List<MasterProductionSchedule> findByStatus(PlanStatus status);

    /**
     * すべての基準生産計画を取得する
     */
    List<MasterProductionSchedule> findAll();

    /**
     * ステータスを更新する
     */
    void updateStatus(Integer id, PlanStatus status);

    /**
     * すべての基準生産計画を削除する
     */
    void deleteAll();
}
