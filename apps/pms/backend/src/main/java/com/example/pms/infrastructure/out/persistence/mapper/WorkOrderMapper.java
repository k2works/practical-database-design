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

    /**
     * ページネーション付きで作業指示を取得する.
     *
     * @param offset オフセット
     * @param limit リミット
     * @param keyword キーワード（オプション）
     * @return 作業指示のリスト
     */
    List<WorkOrder> findWithPagination(@Param("offset") int offset, @Param("limit") int limit,
                                        @Param("keyword") String keyword);

    /**
     * 作業指示の件数を取得する.
     *
     * @param keyword キーワード（オプション）
     * @return 件数
     */
    long count(@Param("keyword") String keyword);

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
    void deleteByWorkOrderNumber(@Param("workOrderNumber") String workOrderNumber);

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
