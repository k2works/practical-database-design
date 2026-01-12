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
