package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateSalesCommand;
import com.example.sms.application.port.in.command.UpdateSalesCommand;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.sales.Sales;
import com.example.sms.domain.model.sales.SalesStatus;

import java.util.List;

/**
 * 売上ユースケース（Input Port）.
 */
public interface SalesUseCase {

    /**
     * 売上を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された売上
     */
    Sales createSales(CreateSalesCommand command);

    /**
     * 売上を更新する.
     *
     * @param salesNumber 売上番号
     * @param command 更新コマンド
     * @return 更新された売上
     */
    Sales updateSales(String salesNumber, UpdateSalesCommand command);

    /**
     * 全売上を取得する.
     *
     * @return 売上リスト
     */
    List<Sales> getAllSales();

    /**
     * ページネーション付きで売上を取得.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード
     * @return ページ結果
     */
    PageResult<Sales> getSales(int page, int size, String keyword);

    /**
     * 売上番号で売上を取得する.
     *
     * @param salesNumber 売上番号
     * @return 売上
     */
    Sales getSalesByNumber(String salesNumber);

    /**
     * ステータスで売上を検索する.
     *
     * @param status 売上ステータス
     * @return 売上リスト
     */
    List<Sales> getSalesByStatus(SalesStatus status);

    /**
     * 顧客コードで売上を検索する.
     *
     * @param customerCode 顧客コード
     * @return 売上リスト
     */
    List<Sales> getSalesByCustomer(String customerCode);

    /**
     * 受注IDで売上を検索する.
     *
     * @param orderId 受注ID
     * @return 売上リスト
     */
    List<Sales> getSalesByOrder(Integer orderId);

    /**
     * 売上を削除する.
     *
     * @param salesNumber 売上番号
     */
    void deleteSales(String salesNumber);

    /**
     * 売上をキャンセルする.
     *
     * @param salesNumber 売上番号
     * @return キャンセルされた売上
     */
    Sales cancelSales(String salesNumber);
}
