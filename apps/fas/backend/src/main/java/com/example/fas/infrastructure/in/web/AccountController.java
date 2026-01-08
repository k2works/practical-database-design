package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.AccountUseCase;
import com.example.fas.application.port.in.dto.AccountResponse;
import com.example.fas.application.port.in.dto.CreateAccountCommand;
import com.example.fas.application.port.in.dto.UpdateAccountCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 勘定科目マスタ REST コントローラー.
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "勘定科目マスタ", description = "勘定科目マスタの管理API")
public class AccountController {

    private final AccountUseCase accountUseCase;

    /**
     * 勘定科目を取得.
     *
     * @param accountCode 勘定科目コード
     * @return 勘定科目レスポンス
     */
    @GetMapping("/{accountCode}")
    @Operation(summary = "勘定科目取得", description = "勘定科目コードで勘定科目を取得します")
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "勘定科目が存在しない")
    public ResponseEntity<AccountResponse> getAccount(
            @Parameter(description = "勘定科目コード")
            @PathVariable String accountCode) {
        return ResponseEntity.ok(accountUseCase.getAccount(accountCode));
    }

    /**
     * 勘定科目一覧を取得.
     *
     * @param bsPlType BSPL区分（オプション）
     * @param postingOnly 計上科目のみ（オプション）
     * @return 勘定科目レスポンスリスト
     */
    @GetMapping
    @Operation(summary = "勘定科目一覧取得", description = "勘定科目の一覧を取得します")
    public ResponseEntity<List<AccountResponse>> getAccounts(
            @Parameter(description = "BSPL区分（BS/PL）")
            @RequestParam(required = false) String bsPlType,
            @Parameter(description = "計上科目のみ")
            @RequestParam(required = false, defaultValue = "false") boolean postingOnly) {

        List<AccountResponse> accounts;
        if (bsPlType != null) {
            accounts = accountUseCase.getAccountsByBsPlType(bsPlType);
        } else if (postingOnly) {
            accounts = accountUseCase.getPostingAccounts();
        } else {
            accounts = accountUseCase.getAllAccounts();
        }
        return ResponseEntity.ok(accounts);
    }

    /**
     * 勘定科目を登録.
     *
     * @param command 登録コマンド
     * @return 勘定科目レスポンス
     */
    @PostMapping
    @Operation(summary = "勘定科目登録", description = "新規勘定科目を登録します")
    @ApiResponse(responseCode = "201", description = "登録成功")
    @ApiResponse(responseCode = "400", description = "バリデーションエラー")
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountCommand command) {
        AccountResponse response = accountUseCase.createAccount(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 勘定科目を更新.
     *
     * @param accountCode 勘定科目コード
     * @param command 更新コマンド
     * @return 勘定科目レスポンス
     */
    @PutMapping("/{accountCode}")
    @Operation(summary = "勘定科目更新", description = "勘定科目を更新します")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable String accountCode,
            @Valid @RequestBody UpdateAccountCommand command) {
        return ResponseEntity.ok(accountUseCase.updateAccount(accountCode, command));
    }

    /**
     * 勘定科目を削除.
     *
     * @param accountCode 勘定科目コード
     * @return 204 No Content
     */
    @DeleteMapping("/{accountCode}")
    @Operation(summary = "勘定科目削除", description = "勘定科目を削除します")
    @ApiResponse(responseCode = "204", description = "削除成功")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountCode) {
        accountUseCase.deleteAccount(accountCode);
        return ResponseEntity.noContent().build();
    }
}
