package com.example.pms.application.port.out;

import com.example.pms.domain.model.purchase.Receiving;

import java.util.List;
import java.util.Optional;

/**
 * 入荷受入データリポジトリ（Output Port）
 */
public interface ReceivingRepository {

    void save(Receiving receiving);

    Optional<Receiving> findById(Integer id);

    Optional<Receiving> findByReceivingNumber(String receivingNumber);

    /**
     * 入荷番号で検索（検査を含む）.
     *
     * @param receivingNumber 入荷番号
     * @return 検査を含む入荷データ
     */
    Optional<Receiving> findByReceivingNumberWithInspections(String receivingNumber);

    List<Receiving> findByPurchaseOrderNumber(String purchaseOrderNumber);

    List<Receiving> findByPurchaseOrderNumberAndLineNumber(
            String purchaseOrderNumber, Integer lineNumber);

    List<Receiving> findAll();

    /**
     * ページネーション付きで入荷受入を取得する.
     *
     * @param offset オフセット
     * @param limit 取得件数
     * @param receivingType 入荷種別（null可）
     * @param keyword キーワード（null可）
     * @return 入荷受入リスト
     */
    List<Receiving> findWithPagination(int offset, int limit, String receivingType, String keyword);

    /**
     * 入荷受入の件数を取得する.
     *
     * @param receivingType 入荷種別（null可）
     * @param keyword キーワード（null可）
     * @return 件数
     */
    long count(String receivingType, String keyword);

    /**
     * 入荷受入を更新する.
     *
     * @param receiving 入荷受入
     */
    void update(Receiving receiving);

    /**
     * 入荷受入を削除する.
     *
     * @param receivingNumber 入荷番号
     */
    void deleteByReceivingNumber(String receivingNumber);

    void deleteAll();
}
