package com.example.pms.infrastructure.in.rest;

import com.example.pms.application.port.in.MrpUseCase;
import com.example.pms.infrastructure.in.rest.dto.ExecuteMrpRequest;
import com.example.pms.infrastructure.in.rest.dto.MrpResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MRP API Controller.
 */
@RestController
@RequestMapping("/api/mrp")
@Tag(name = "mrp", description = "MRP API")
public class MrpController {

    private final MrpUseCase mrpUseCase;

    public MrpController(MrpUseCase mrpUseCase) {
        this.mrpUseCase = mrpUseCase;
    }

    /**
     * MRP（所要量展開）を実行する.
     *
     * @param request 実行リクエスト
     * @return MRP 実行結果
     */
    @PostMapping("/execute")
    @Operation(
        summary = "MRP の実行",
        description = "指定期間の所要量展開を実行し、計画オーダを生成します"
    )
    public ResponseEntity<MrpResultResponse> execute(@Valid @RequestBody ExecuteMrpRequest request) {
        MrpUseCase.MrpResult result = mrpUseCase.execute(
            request.getStartDate(),
            request.getEndDate()
        );
        return ResponseEntity.ok(MrpResultResponse.from(result));
    }

    /**
     * MRP 実行結果を照会する（未実装）.
     *
     * @return ステータスメッセージ
     */
    @GetMapping("/results")
    @Operation(summary = "MRP 実行結果の照会")
    public ResponseEntity<String> getResults() {
        return ResponseEntity.ok("未実装");
    }

    /**
     * 計画オーダ一覧を取得する（未実装）.
     *
     * @return ステータスメッセージ
     */
    @GetMapping("/planned-orders")
    @Operation(summary = "計画オーダ一覧の取得")
    public ResponseEntity<String> getPlannedOrders() {
        return ResponseEntity.ok("未実装");
    }
}
