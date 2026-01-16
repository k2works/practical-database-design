package com.example.pms.application.port.out;

import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * 発注データリポジトリ（Output Port）
 */
public interface PurchaseOrderRepository {

    void save(PurchaseOrder purchaseOrder);

    Optional<PurchaseOrder> findById(Integer id);

    Optional<PurchaseOrder> findByPurchaseOrderNumber(String purchaseOrderNumber);

    /**
     * 発注番号で検索（明細を含む）.
     *
     * @param purchaseOrderNumber 発注番号
     * @return 明細を含む発注データ
     */
    Optional<PurchaseOrder> findByPurchaseOrderNumberWithDetails(String purchaseOrderNumber);

    List<PurchaseOrder> findBySupplierCode(String supplierCode);

    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    List<PurchaseOrder> findAll();

    void updateStatus(Integer id, PurchaseOrderStatus status);

    void deleteAll();

    /**
     * ページネーション付きで発注を取得.
     *
     * @param status ステータス（null可）
     * @param limit 取得件数
     * @param offset オフセット
     * @return 発注リスト
     */
    List<PurchaseOrder> findWithPagination(PurchaseOrderStatus status, int limit, int offset);

    /**
     * 条件に一致する発注の件数を取得.
     *
     * @param status ステータス（null可）
     * @return 件数
     */
    long count(PurchaseOrderStatus status);
}
