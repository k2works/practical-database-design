package com.example.pms.infrastructure.in.rest;

import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
import com.example.pms.infrastructure.in.rest.dto.CreateWorkOrderRequest;
import com.example.pms.infrastructure.in.rest.dto.RecordCompletionRequest;
import com.example.pms.infrastructure.in.rest.dto.UpdateProgressRequest;
import com.example.pms.infrastructure.in.rest.dto.WorkOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 作業指示 API Controller.
 */
@RestController
@RequestMapping("/api/work-orders")
@Tag(name = "work-orders", description = "作業指示 API")
public class WorkOrderController {

    private final WorkOrderUseCase workOrderUseCase;

    public WorkOrderController(WorkOrderUseCase workOrderUseCase) {
        this.workOrderUseCase = workOrderUseCase;
    }

    /**
     * 作業指示一覧を取得する.
     *
     * @param status ステータス（オプション）
     * @return 作業指示リスト
     */
    @GetMapping
    @Operation(summary = "作業指示一覧の取得")
    public ResponseEntity<List<WorkOrderResponse>> getAllWorkOrders(
            @Parameter(description = "ステータスでフィルタ")
            @RequestParam(required = false) WorkOrderStatus status) {
        List<WorkOrder> workOrders;
        if (status != null) {
            workOrders = workOrderUseCase.getWorkOrdersByStatus(status);
        } else {
            workOrders = workOrderUseCase.getAllWorkOrders();
        }
        return ResponseEntity.ok(workOrders.stream()
            .map(WorkOrderResponse::from)
            .toList());
    }

    /**
     * 作業指示を取得する.
     *
     * @param workOrderNumber 作業指示番号
     * @return 作業指示
     */
    @GetMapping("/{workOrderNumber}")
    @Operation(summary = "作業指示詳細の取得")
    public ResponseEntity<WorkOrderResponse> getWorkOrder(@PathVariable String workOrderNumber) {
        return workOrderUseCase.getWorkOrder(workOrderNumber)
            .map(workOrder -> ResponseEntity.ok(WorkOrderResponse.from(workOrder)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 作業指示を登録する.
     *
     * @param request 登録リクエスト
     * @return 登録した作業指示
     */
    @PostMapping
    @Operation(summary = "作業指示の登録")
    public ResponseEntity<WorkOrderResponse> createWorkOrder(
            @Valid @RequestBody CreateWorkOrderRequest request) {
        WorkOrder workOrder = workOrderUseCase.createWorkOrder(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(WorkOrderResponse.from(workOrder));
    }

    /**
     * 完成実績を登録する.
     *
     * @param workOrderNumber 作業指示番号
     * @param request 完成実績リクエスト
     * @return 更新した作業指示
     */
    @PostMapping("/{workOrderNumber}/completion")
    @Operation(summary = "完成実績の登録")
    public ResponseEntity<WorkOrderResponse> recordCompletion(
            @PathVariable String workOrderNumber,
            @Valid @RequestBody RecordCompletionRequest request) {
        WorkOrder workOrder = workOrderUseCase.recordCompletion(workOrderNumber, request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(WorkOrderResponse.from(workOrder));
    }

    /**
     * 作業進捗を更新する.
     *
     * @param workOrderNumber 作業指示番号
     * @param request 進捗更新リクエスト
     * @return 更新した作業指示
     */
    @PatchMapping("/{workOrderNumber}/progress")
    @Operation(summary = "作業進捗の更新")
    public ResponseEntity<WorkOrderResponse> updateProgress(
            @PathVariable String workOrderNumber,
            @Valid @RequestBody UpdateProgressRequest request) {
        WorkOrder workOrder = workOrderUseCase.updateProgress(workOrderNumber, request.getStatus());
        return ResponseEntity.ok(WorkOrderResponse.from(workOrder));
    }
}
