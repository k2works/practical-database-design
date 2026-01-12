package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作業指示 Mapper.
 */
@Mapper
public interface WorkOrderMapper {

    void insert(WorkOrder workOrder);

    WorkOrder findById(Integer id);

    WorkOrder findByWorkOrderNumber(String workOrderNumber);

    List<WorkOrder> findByOrderNumber(String orderNumber);

    List<WorkOrder> findByStatus(@Param("status") WorkOrderStatus status);

    List<WorkOrder> findAll();

    void deleteAll();

    /**
     * 完成数量を更新（楽観ロック対応）.
     *
     * @param workOrderNumber  作業指示番号
     * @param completedQuantity 完成数量（追加分）
     * @param goodQuantity      良品数（追加分）
     * @param defectQuantity    不良品数（追加分）
     * @param version           期待するバージョン
     * @return 更新行数（0 ならバージョン競合）
     */
    int updateCompletionQuantities(@Param("workOrderNumber") String workOrderNumber,
                                   @Param("completedQuantity") BigDecimal completedQuantity,
                                   @Param("goodQuantity") BigDecimal goodQuantity,
                                   @Param("defectQuantity") BigDecimal defectQuantity,
                                   @Param("version") Integer version);
}
