package com.example.sms.application.port.in;

import com.example.sms.domain.model.purchase.Receiving;
import com.example.sms.domain.model.purchase.ReceivingStatus;

import java.util.List;

/**
 * 入荷ユースケース（Input Port）.
 */
public interface ReceivingUseCase {

    /**
     * 全入荷を取得する.
     *
     * @return 入荷リスト
     */
    List<Receiving> getAllReceivings();

    /**
     * 入荷番号で入荷を取得する.
     *
     * @param receivingNumber 入荷番号
     * @return 入荷
     */
    Receiving getReceivingByNumber(String receivingNumber);

    /**
     * 入荷番号で入荷（明細含む）を取得する.
     *
     * @param receivingNumber 入荷番号
     * @return 入荷（明細含む）
     */
    Receiving getReceivingWithDetails(String receivingNumber);

    /**
     * ステータスで入荷を検索する.
     *
     * @param status 入荷ステータス
     * @return 入荷リスト
     */
    List<Receiving> getReceivingsByStatus(ReceivingStatus status);

    /**
     * 仕入先コードで入荷を検索する.
     *
     * @param supplierCode 仕入先コード
     * @return 入荷リスト
     */
    List<Receiving> getReceivingsBySupplier(String supplierCode);

    /**
     * 発注IDで入荷を検索する.
     *
     * @param purchaseOrderId 発注ID
     * @return 入荷リスト
     */
    List<Receiving> getReceivingsByPurchaseOrder(Integer purchaseOrderId);
}
