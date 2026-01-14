package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.PlanStatus;

import java.util.List;
import java.util.Optional;

/**
 * 基準生産計画ユースケース（Input Port）.
 */
public interface MpsUseCase {

    /**
     * ページネーション付きで基準生産計画一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param status ステータス（オプション）
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<MasterProductionSchedule> getMpsList(int page, int size, PlanStatus status, String keyword);

    /**
     * すべての基準生産計画を取得する.
     *
     * @return 基準生産計画のリスト
     */
    List<MasterProductionSchedule> getAllMps();

    /**
     * MPS番号で基準生産計画を取得する.
     *
     * @param mpsNumber MPS番号
     * @return 基準生産計画
     */
    Optional<MasterProductionSchedule> getMps(String mpsNumber);

    /**
     * MPS番号で基準生産計画を取得する（オーダを含む）.
     *
     * @param mpsNumber MPS番号
     * @return オーダを含む基準生産計画
     */
    Optional<MasterProductionSchedule> getMpsWithOrders(String mpsNumber);

    /**
     * 基準生産計画を登録する.
     *
     * @param mps 基準生産計画
     * @return 登録された基準生産計画
     */
    MasterProductionSchedule createMps(MasterProductionSchedule mps);

    /**
     * 基準生産計画を更新する.
     *
     * @param mpsNumber MPS番号
     * @param mps 更新内容
     * @return 更新された基準生産計画
     */
    MasterProductionSchedule updateMps(String mpsNumber, MasterProductionSchedule mps);

    /**
     * 基準生産計画を確定する.
     *
     * @param mpsNumber MPS番号
     */
    void confirmMps(String mpsNumber);

    /**
     * 基準生産計画を取消する.
     *
     * @param mpsNumber MPS番号
     */
    void cancelMps(String mpsNumber);
}
