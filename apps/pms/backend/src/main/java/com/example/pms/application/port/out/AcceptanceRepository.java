package com.example.pms.application.port.out;

import com.example.pms.domain.model.purchase.Acceptance;

import java.util.List;
import java.util.Optional;

/**
 * 検収データリポジトリ（Output Port）
 */
public interface AcceptanceRepository {

    void save(Acceptance acceptance);

    Optional<Acceptance> findById(Integer id);

    Optional<Acceptance> findByAcceptanceNumber(String acceptanceNumber);

    List<Acceptance> findByInspectionNumber(String inspectionNumber);

    List<Acceptance> findByPurchaseOrderNumber(String purchaseOrderNumber);

    List<Acceptance> findAll();

    /**
     * ページネーション付きで検収を取得する.
     *
     * @param offset オフセット
     * @param limit 取得件数
     * @param keyword キーワード（null可）
     * @return 検収リスト
     */
    List<Acceptance> findWithPagination(int offset, int limit, String keyword);

    /**
     * 検収の件数を取得する.
     *
     * @param keyword キーワード（null可）
     * @return 件数
     */
    long count(String keyword);

    /**
     * 検収を更新する.
     *
     * @param acceptance 検収
     */
    void update(Acceptance acceptance);

    /**
     * 検収を削除する.
     *
     * @param acceptanceNumber 検収番号
     */
    void deleteByAcceptanceNumber(String acceptanceNumber);

    void deleteAll();
}
