package com.example.fas.application.port.in;

import com.example.fas.application.port.in.dto.CreateTaxTransactionCommand;
import com.example.fas.application.port.in.dto.TaxTransactionResponse;
import com.example.fas.application.port.in.dto.UpdateTaxTransactionCommand;
import com.example.fas.domain.model.common.PageResult;
import java.util.List;

/**
 * 課税取引ユースケース（Input Port）.
 */
public interface TaxTransactionUseCase {

    /**
     * 課税取引を取得.
     *
     * @param taxCode 課税取引コード
     * @return 課税取引レスポンス
     */
    TaxTransactionResponse getTaxTransaction(String taxCode);

    /**
     * 全課税取引を取得.
     *
     * @return 課税取引レスポンスリスト
     */
    List<TaxTransactionResponse> getAllTaxTransactions();

    /**
     * ページネーション付きで課税取引を取得.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param keyword キーワード
     * @return ページネーション結果
     */
    PageResult<TaxTransactionResponse> getTaxTransactions(int page, int size, String keyword);

    /**
     * 課税取引を登録.
     *
     * @param command 登録コマンド
     * @return 課税取引レスポンス
     */
    TaxTransactionResponse createTaxTransaction(CreateTaxTransactionCommand command);

    /**
     * 課税取引を更新.
     *
     * @param taxCode 課税取引コード
     * @param command 更新コマンド
     * @return 課税取引レスポンス
     */
    TaxTransactionResponse updateTaxTransaction(String taxCode, UpdateTaxTransactionCommand command);

    /**
     * 課税取引を削除.
     *
     * @param taxCode 課税取引コード
     */
    void deleteTaxTransaction(String taxCode);
}
