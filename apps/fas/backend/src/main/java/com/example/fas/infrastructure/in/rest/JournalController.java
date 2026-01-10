package com.example.fas.infrastructure.in.rest;

import com.example.fas.application.port.in.JournalUseCase;
import com.example.fas.application.port.in.command.CreateJournalCommand;
import com.example.fas.application.port.in.dto.JournalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仕訳 REST コントローラ.
 */
@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
@Tag(name = "仕訳", description = "仕訳の登録・照会・取消・削除")
public class JournalController {

    private final JournalUseCase journalUseCase;

    /**
     * 仕訳を取得.
     *
     * @param voucherNumber 仕訳伝票番号
     * @return 仕訳レスポンス
     */
    @GetMapping("/{voucherNumber}")
    @Operation(summary = "仕訳取得", description = "指定された仕訳伝票番号の仕訳を取得します")
    public ResponseEntity<JournalResponse> getJournal(
            @Parameter(description = "仕訳伝票番号") @PathVariable String voucherNumber) {
        return ResponseEntity.ok(journalUseCase.getJournal(voucherNumber));
    }

    /**
     * 期間指定で仕訳を検索.
     *
     * @param fromDate 開始日
     * @param toDate 終了日
     * @return 仕訳レスポンスリスト
     */
    @GetMapping
    @Operation(summary = "仕訳検索", description = "期間を指定して仕訳を検索します")
    public ResponseEntity<List<JournalResponse>> getJournals(
            @Parameter(description = "開始日") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "終了日") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(journalUseCase.getJournalsByDateRange(fromDate, toDate));
    }

    /**
     * 勘定科目コードで仕訳を検索.
     *
     * @param accountCode 勘定科目コード
     * @return 仕訳レスポンスリスト
     */
    @GetMapping("/by-account/{accountCode}")
    @Operation(summary = "勘定科目別仕訳検索",
            description = "指定された勘定科目コードを含む仕訳を検索します")
    public ResponseEntity<List<JournalResponse>> getJournalsByAccountCode(
            @Parameter(description = "勘定科目コード") @PathVariable String accountCode) {
        return ResponseEntity.ok(journalUseCase.getJournalsByAccountCode(accountCode));
    }

    /**
     * 仕訳を登録.
     *
     * @param command 登録コマンド
     * @return 登録した仕訳
     */
    @PostMapping
    @Operation(summary = "仕訳登録", description = "新しい仕訳を登録します")
    public ResponseEntity<JournalResponse> createJournal(
            @Valid @RequestBody CreateJournalCommand command) {
        JournalResponse response = journalUseCase.createJournal(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 仕訳を取消（赤伝処理）.
     *
     * @param voucherNumber 取消対象の仕訳伝票番号
     * @return 赤伝票
     */
    @PostMapping("/{voucherNumber}/cancel")
    @Operation(summary = "仕訳取消", description = "指定された仕訳を取消（赤伝処理）します")
    public ResponseEntity<JournalResponse> cancelJournal(
            @Parameter(description = "仕訳伝票番号") @PathVariable String voucherNumber) {
        return ResponseEntity.ok(journalUseCase.cancelJournal(voucherNumber));
    }

    /**
     * 仕訳を削除.
     *
     * @param voucherNumber 仕訳伝票番号
     * @return 204 No Content
     */
    @DeleteMapping("/{voucherNumber}")
    @Operation(summary = "仕訳削除", description = "指定された仕訳を削除します")
    public ResponseEntity<Void> deleteJournal(
            @Parameter(description = "仕訳伝票番号") @PathVariable String voucherNumber) {
        journalUseCase.deleteJournal(voucherNumber);
        return ResponseEntity.noContent().build();
    }
}
