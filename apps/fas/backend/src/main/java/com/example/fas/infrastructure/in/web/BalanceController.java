package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.BalanceUseCase;
import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.application.port.in.dto.TrialBalanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 残高照会 REST コントローラ.
 */
@RestController
@RequestMapping("/api/balances")
@RequiredArgsConstructor
@Tag(name = "残高照会", description = "試算表・月次残高の照会")
public class BalanceController {

    private final BalanceUseCase balanceUseCase;

    /**
     * 合計残高試算表を取得.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @param bsPlType BSPL区分（オプション）
     * @return 試算表レスポンス
     */
    @GetMapping("/trial-balance")
    @Operation(summary = "合計残高試算表取得", description = "指定された月度の合計残高試算表を取得します")
    public ResponseEntity<TrialBalanceResponse> getTrialBalance(
            @Parameter(description = "決算期（年度）") @RequestParam Integer fiscalYear,
            @Parameter(description = "月度（1-12）") @RequestParam Integer month,
            @Parameter(description = "BSPL区分（BS/PL）") @RequestParam(required = false) String bsPlType) {
        TrialBalanceResponse response;
        if (bsPlType != null && !bsPlType.isEmpty()) {
            response = balanceUseCase.getTrialBalanceByBsPlType(fiscalYear, month, bsPlType);
        } else {
            response = balanceUseCase.getTrialBalance(fiscalYear, month);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 月次残高一覧を取得.
     *
     * @param fiscalYear 決算期
     * @param month 月度
     * @return 月次残高リスト
     */
    @GetMapping("/monthly")
    @Operation(summary = "月次残高一覧取得", description = "指定された月度の月次残高一覧を取得します")
    public ResponseEntity<List<MonthlyBalanceResponse>> getMonthlyBalances(
            @Parameter(description = "決算期（年度）") @RequestParam Integer fiscalYear,
            @Parameter(description = "月度（1-12）") @RequestParam Integer month) {
        List<MonthlyBalanceResponse> responses =
                balanceUseCase.getMonthlyBalances(fiscalYear, month);
        return ResponseEntity.ok(responses);
    }

    /**
     * 勘定科目別の月次残高推移を取得.
     *
     * @param accountCode 勘定科目コード
     * @param fiscalYear 決算期
     * @return 月次残高リスト（月別）
     */
    @GetMapping("/monthly/account/{accountCode}")
    @Operation(summary = "勘定科目別月次残高推移取得",
            description = "指定された勘定科目の月次残高推移を取得します")
    public ResponseEntity<List<MonthlyBalanceResponse>> getMonthlyBalancesByAccountCode(
            @Parameter(description = "勘定科目コード") @PathVariable String accountCode,
            @Parameter(description = "決算期（年度）") @RequestParam Integer fiscalYear) {
        List<MonthlyBalanceResponse> responses =
                balanceUseCase.getMonthlyBalancesByAccountCode(fiscalYear, accountCode);
        return ResponseEntity.ok(responses);
    }
}
