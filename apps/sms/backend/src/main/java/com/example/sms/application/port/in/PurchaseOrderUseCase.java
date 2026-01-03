package com.example.sms.application.port.in;

import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;

import java.util.List;

/**
 * 発注ユースケース（Input Port）.
 */
public interface PurchaseOrderUseCase {

    /**
     * 全発注を取得する.
     *
     * @return 発注リスト
     */
    List<PurchaseOrder> getAllPurchaseOrders();

    /**
     * 発注番号で発注を取得する.
     *
     * @param purchaseOrderNumber 発注番号
     * @return 発注
     */
    PurchaseOrder getPurchaseOrderByNumber(String purchaseOrderNumber);

    /**
     * 発注番号で発注（明細含む）を取得する.
     *
     * @param purchaseOrderNumber 発注番号
     * @return 発注（明細含む）
     */
    PurchaseOrder getPurchaseOrderWithDetails(String purchaseOrderNumber);

    /**
     * ステータスで発注を検索する.
     *
     * @param status 発注ステータス
     * @return 発注リスト
     */
    List<PurchaseOrder> getPurchaseOrdersByStatus(PurchaseOrderStatus status);

    /**
     * 仕入先コードで発注を検索する.
     *
     * @param supplierCode 仕入先コード
     * @return 発注リスト
     */
    List<PurchaseOrder> getPurchaseOrdersBySupplier(String supplierCode);
}
