package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreatePurchaseOrderCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;

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
    List<PurchaseOrder> getAllOrders();

    /**
     * 発注番号で発注を取得する.
     *
     * @param orderNumber 発注番号
     * @return 発注
     */
    PurchaseOrder getOrder(String orderNumber);

    /**
     * 発注を登録する.
     *
     * @param command 登録コマンド
     * @return 登録した発注
     */
    PurchaseOrder createOrder(CreatePurchaseOrderCommand command);

    /**
     * 発注を確定する.
     *
     * @param orderNumber 発注番号
     * @return 確定した発注
     */
    PurchaseOrder confirmOrder(String orderNumber);

    /**
     * 発注を取消する.
     *
     * @param orderNumber 発注番号
     */
    void cancelOrder(String orderNumber);

    /**
     * ステータスで発注を検索する.
     *
     * @param status ステータス
     * @return 発注リスト
     */
    List<PurchaseOrder> getOrdersByStatus(PurchaseOrderStatus status);

    /**
     * ページネーション付きで発注を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param status ステータス（null可）
     * @return ページネーション結果
     */
    PageResult<PurchaseOrder> getOrders(int page, int size, PurchaseOrderStatus status);
}
