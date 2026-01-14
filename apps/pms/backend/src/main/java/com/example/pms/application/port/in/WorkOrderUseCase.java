package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreateWorkOrderCommand;
import com.example.pms.application.port.in.command.RecordCompletionCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * 作業指示ユースケース（Input Port）.
 */
public interface WorkOrderUseCase {

    /**
     * ページネーション付きで作業指示一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<WorkOrder> getWorkOrderList(int page, int size, String keyword);

    /**
     * 全作業指示を取得する.
     *
     * @return 作業指示リスト
     */
    List<WorkOrder> getAllWorkOrders();

    /**
     * 作業指示番号で作業指示を取得する.
     *
     * @param workOrderNumber 作業指示番号
     * @return 作業指示
     */
    Optional<WorkOrder> getWorkOrder(String workOrderNumber);

    /**
     * 作業指示を登録する.
     *
     * @param workOrder 作業指示
     * @return 登録した作業指示
     */
    WorkOrder createWorkOrder(WorkOrder workOrder);

    /**
     * 作業指示を登録する（コマンド版）.
     *
     * @param command 登録コマンド
     * @return 登録した作業指示
     */
    WorkOrder createWorkOrder(CreateWorkOrderCommand command);

    /**
     * 作業指示を更新する.
     *
     * @param workOrderNumber 作業指示番号
     * @param workOrder 作業指示
     * @return 更新した作業指示
     */
    WorkOrder updateWorkOrder(String workOrderNumber, WorkOrder workOrder);

    /**
     * 作業指示を削除する.
     *
     * @param workOrderNumber 作業指示番号
     */
    void deleteWorkOrder(String workOrderNumber);

    /**
     * 完成実績を登録する.
     *
     * @param workOrderNumber 作業指示番号
     * @param command 完成実績コマンド
     * @return 更新した作業指示
     */
    WorkOrder recordCompletion(String workOrderNumber, RecordCompletionCommand command);

    /**
     * 作業進捗を更新する.
     *
     * @param workOrderNumber 作業指示番号
     * @param status 新しいステータス
     * @return 更新した作業指示
     */
    WorkOrder updateProgress(String workOrderNumber, WorkOrderStatus status);

    /**
     * ステータスで作業指示を検索する.
     *
     * @param status ステータス
     * @return 作業指示リスト
     */
    List<WorkOrder> getWorkOrdersByStatus(WorkOrderStatus status);
}
