package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 作業指示リポジトリ.
 */
public interface WorkOrderRepository {

    void save(WorkOrder workOrder);

    Optional<WorkOrder> findById(Integer id);

    Optional<WorkOrder> findByWorkOrderNumber(String workOrderNumber);

    List<WorkOrder> findByOrderNumber(String orderNumber);

    List<WorkOrder> findByStatus(WorkOrderStatus status);

    List<WorkOrder> findAll();

    /**
     * ページネーション付きで作業指示を取得する.
     *
     * @param offset オフセット
     * @param limit リミット
     * @param keyword キーワード（オプション）
     * @return 作業指示のリスト
     */
    List<WorkOrder> findWithPagination(int offset, int limit, String keyword);

    /**
     * 作業指示の件数を取得する.
     *
     * @param keyword キーワード（オプション）
     * @return 件数
     */
    long count(String keyword);

    /**
     * 作業指示を更新する.
     *
     * @param workOrder 作業指示
     */
    void update(WorkOrder workOrder);

    /**
     * 作業指示番号で削除する.
     *
     * @param workOrderNumber 作業指示番号
     */
    void deleteByWorkOrderNumber(String workOrderNumber);

    void deleteAll();

    /**
     * 完成数量を更新（楽観ロック対応）.
     *
     * @param workOrderNumber   作業指示番号
     * @param completedQuantity 完成数量（追加分）
     * @param goodQuantity      良品数（追加分）
     * @param defectQuantity    不良品数（追加分）
     * @param expectedVersion   期待するバージョン
     * @return 更新成功時 true、バージョン競合時 false
     */
    boolean updateCompletionQuantities(String workOrderNumber, BigDecimal completedQuantity,
                                       BigDecimal goodQuantity, BigDecimal defectQuantity,
                                       Integer expectedVersion);
}
