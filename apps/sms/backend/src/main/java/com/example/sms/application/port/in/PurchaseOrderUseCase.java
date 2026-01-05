package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreatePurchaseOrderCommand;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.purchase.PurchaseOrderStatus;

import java.util.List;

/**
 * 発注ユースケース（Input Port）.
 */
public interface PurchaseOrderUseCase {

    /**
     * 発注を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された発注
     */
    PurchaseOrder createPurchaseOrder(CreatePurchaseOrderCommand command);

    /**
     * 全発注を取得する.
     *
     * @return 発注リスト
     */
    List<PurchaseOrder> getAllPurchaseOrders();

    /**
     * ページネーション付きで発注を取得.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード
     * @return ページ結果
     */
    PageResult<PurchaseOrder> getPurchaseOrders(int page, int size, String keyword);

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
