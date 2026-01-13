package com.example.pms.infrastructure.in.rest;

import com.example.pms.application.port.in.BomUseCase;
import com.example.pms.domain.model.bom.Bom;
import com.example.pms.domain.model.bom.BomExplosion;
import com.example.pms.infrastructure.in.rest.dto.BomExplosionResponse;
import com.example.pms.infrastructure.in.rest.dto.BomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * BOM API Controller.
 */
@RestController
@RequestMapping("/api/bom")
@Tag(name = "bom", description = "BOM API")
public class BomController {

    private final BomUseCase bomUseCase;

    public BomController(BomUseCase bomUseCase) {
        this.bomUseCase = bomUseCase;
    }

    /**
     * BOM を取得する（親品目コード指定）.
     *
     * @param itemCode 親品目コード
     * @return BOM リスト
     */
    @GetMapping("/{itemCode}")
    @Operation(summary = "BOM の取得")
    public ResponseEntity<List<BomResponse>> getBom(@PathVariable String itemCode) {
        List<Bom> boms = bomUseCase.getBomByParentItem(itemCode);
        return ResponseEntity.ok(boms.stream()
            .map(BomResponse::from)
            .toList());
    }

    /**
     * BOM 展開（部品展開）を実行する.
     *
     * @param itemCode 品目コード
     * @param quantity 数量（デフォルト: 1）
     * @return 展開結果
     */
    @GetMapping("/{itemCode}/explode")
    @Operation(summary = "BOM 展開（部品展開）")
    public ResponseEntity<List<BomExplosionResponse>> explodeBom(
            @PathVariable String itemCode,
            @Parameter(description = "展開数量（デフォルト: 1）")
            @RequestParam(defaultValue = "1") BigDecimal quantity) {
        List<BomExplosion> explosions = bomUseCase.explodeBom(itemCode, quantity);
        return ResponseEntity.ok(explosions.stream()
            .map(BomExplosionResponse::from)
            .toList());
    }

    /**
     * 逆展開（使用先照会）を実行する.
     *
     * @param itemCode 子品目コード
     * @return 使用先リスト
     */
    @GetMapping("/{itemCode}/where-used")
    @Operation(summary = "逆展開（使用先照会）")
    public ResponseEntity<List<BomResponse>> whereUsed(@PathVariable String itemCode) {
        List<Bom> boms = bomUseCase.whereUsed(itemCode);
        return ResponseEntity.ok(boms.stream()
            .map(BomResponse::from)
            .toList());
    }
}
