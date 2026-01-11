package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateOrderCommand;
import com.example.sms.application.port.in.command.UpdateOrderCommand;
import com.example.sms.application.port.in.dto.OrderImportResult;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;

import java.io.InputStream;
import java.util.List;

/**
 * 受注ユースケース（Input Port）.
 */
public interface OrderUseCase {

    /**
     * 受注を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された受注
     */
    SalesOrder createOrder(CreateOrderCommand command);

    /**
     * 受注を更新する.
     *
     * @param orderNumber 受注番号
     * @param command 更新コマンド
     * @return 更新された受注
     */
    SalesOrder updateOrder(String orderNumber, UpdateOrderCommand command);

    /**
     * 全受注を取得する.
     *
     * @return 受注リスト
     */
    List<SalesOrder> getAllOrders();

    /**
     * ページネーション付きで受注を取得.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード
     * @return ページ結果
     */
    PageResult<SalesOrder> getOrders(int page, int size, String keyword);

    /**
     * 受注番号で受注を取得する.
     *
     * @param orderNumber 受注番号
     * @return 受注
     */
    SalesOrder getOrderByNumber(String orderNumber);

    /**
     * 受注番号で受注（明細含む）を取得する.
     *
     * @param orderNumber 受注番号
     * @return 受注（明細含む）
     */
    SalesOrder getOrderWithDetails(String orderNumber);

    /**
     * ステータスで受注を検索する.
     *
     * @param status 受注ステータス
     * @return 受注リスト
     */
    List<SalesOrder> getOrdersByStatus(OrderStatus status);

    /**
     * 顧客コードで受注を検索する.
     *
     * @param customerCode 顧客コード
     * @return 受注リスト
     */
    List<SalesOrder> getOrdersByCustomer(String customerCode);

    /**
     * 受注を削除する.
     *
     * @param orderNumber 受注番号
     */
    void deleteOrder(String orderNumber);

    /**
     * 受注をキャンセルする.
     *
     * @param orderNumber 受注番号
     * @param version 楽観ロック用バージョン
     * @return キャンセルされた受注
     */
    SalesOrder cancelOrder(String orderNumber, Integer version);

    /**
     * CSV ファイルから受注を取り込む.
     *
     * @param inputStream CSV ファイルの入力ストリーム
     * @param skipHeaderLine ヘッダー行をスキップするか
     * @param skipEmptyLines 空行をスキップするか
     * @return 取込結果
     */
    OrderImportResult importOrdersFromCsv(InputStream inputStream, boolean skipHeaderLine, boolean skipEmptyLines);
}
