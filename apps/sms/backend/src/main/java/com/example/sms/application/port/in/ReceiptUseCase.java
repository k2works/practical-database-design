package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateReceiptCommand;
import com.example.sms.application.port.in.command.UpdateReceiptCommand;
import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptStatus;

import java.util.List;

/**
 * 入金ユースケース（Input Port）.
 */
public interface ReceiptUseCase {

    /**
     * 入金を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された入金
     */
    Receipt createReceipt(CreateReceiptCommand command);

    /**
     * 入金を更新する.
     *
     * @param receiptNumber 入金番号
     * @param command 更新コマンド
     * @return 更新された入金
     */
    Receipt updateReceipt(String receiptNumber, UpdateReceiptCommand command);

    /**
     * 全入金を取得する.
     *
     * @return 入金リスト
     */
    List<Receipt> getAllReceipts();

    /**
     * 入金番号で入金を取得する.
     *
     * @param receiptNumber 入金番号
     * @return 入金
     */
    Receipt getReceiptByNumber(String receiptNumber);

    /**
     * ステータスで入金を検索する.
     *
     * @param status 入金ステータス
     * @return 入金リスト
     */
    List<Receipt> getReceiptsByStatus(ReceiptStatus status);

    /**
     * 顧客コードで入金を検索する.
     *
     * @param customerCode 顧客コード
     * @return 入金リスト
     */
    List<Receipt> getReceiptsByCustomer(String customerCode);

    /**
     * 入金を削除する.
     *
     * @param receiptNumber 入金番号
     */
    void deleteReceipt(String receiptNumber);
}
