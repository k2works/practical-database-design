package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.BalanceUseCase;
import com.example.fas.application.port.in.dto.MonthlyBalanceResponse;
import com.example.fas.application.port.in.dto.TrialBalanceResponse;
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
    public ResponseEntity<TrialBalanceResponse> getTrialBalance(
            @RequestParam Integer fiscalYear,
            @RequestParam Integer month,
            @RequestParam(required = false) String bsPlType) {
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
    public ResponseEntity<List<MonthlyBalanceResponse>> getMonthlyBalances(
            @RequestParam Integer fiscalYear,
            @RequestParam Integer month) {
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
    public ResponseEntity<List<MonthlyBalanceResponse>> getMonthlyBalancesByAccountCode(
            @PathVariable String accountCode,
            @RequestParam Integer fiscalYear) {
        List<MonthlyBalanceResponse> responses =
                balanceUseCase.getMonthlyBalancesByAccountCode(fiscalYear, accountCode);
        return ResponseEntity.ok(responses);
    }
}
