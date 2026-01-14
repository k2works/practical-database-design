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
     * MPS番号で基準生産計画を検索する（オーダを含む）
     *
     * @param mpsNumber MPS番号
     * @return オーダを含む基準生産計画
     */
    Optional<MasterProductionSchedule> findByMpsNumberWithOrders(String mpsNumber);

    /**
     * ステータスで基準生産計画を検索する
     */
    List<MasterProductionSchedule> findByStatus(PlanStatus status);

    /**
     * すべての基準生産計画を取得する
     */
    List<MasterProductionSchedule> findAll();

    /**
     * ページネーション付きで基準生産計画を検索する
     *
     * @param status ステータス（オプション）
     * @param keyword キーワード（MPS番号または品目コード）
     * @param limit 取得件数
     * @param offset オフセット
     * @return 基準生産計画のリスト
     */
    List<MasterProductionSchedule> findWithPagination(PlanStatus status, String keyword, int limit, int offset);

    /**
     * 検索条件に合致する基準生産計画の件数を取得する
     *
     * @param status ステータス（オプション）
     * @param keyword キーワード（MPS番号または品目コード）
     * @return 件数
     */
    long count(PlanStatus status, String keyword);

    /**
     * 基準生産計画を更新する
     *
     * @param mps 更新内容
     */
    void update(MasterProductionSchedule mps);

    /**
     * ステータスを更新する
     */
    void updateStatus(Integer id, PlanStatus status);

    /**
     * すべての基準生産計画を削除する
     */
    void deleteAll();
}
