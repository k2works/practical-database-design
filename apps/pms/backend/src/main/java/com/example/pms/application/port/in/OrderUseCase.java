package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.plan.Order;
import com.example.pms.domain.model.plan.PlanStatus;

import java.util.List;
import java.util.Optional;

/**
 * オーダ情報ユースケース（Input Port）.
 */
public interface OrderUseCase {

    /**
     * ページネーション付きでオーダ一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param status ステータス（オプション）
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<Order> getOrders(int page, int size, PlanStatus status, String keyword);

    /**
     * すべてのオーダを取得する.
     *
     * @return オーダのリスト
     */
    List<Order> getAllOrders();

    /**
     * オーダ番号でオーダを取得する.
     *
     * @param orderNumber オーダ番号
     * @return オーダ
     */
    Optional<Order> getOrder(String orderNumber);

    /**
     * オーダ番号でオーダを取得する（所要を含む）.
     *
     * @param orderNumber オーダ番号
     * @return 所要を含むオーダ
     */
    Optional<Order> getOrderWithRequirements(String orderNumber);
}
