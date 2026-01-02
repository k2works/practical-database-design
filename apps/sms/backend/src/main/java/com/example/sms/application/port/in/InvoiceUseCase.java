package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateInvoiceCommand;
import com.example.sms.application.port.in.command.UpdateInvoiceCommand;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceStatus;

import java.util.List;

/**
 * 請求ユースケース（Input Port）.
 */
public interface InvoiceUseCase {

    /**
     * 請求を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された請求
     */
    Invoice createInvoice(CreateInvoiceCommand command);

    /**
     * 請求を更新する.
     *
     * @param invoiceNumber 請求番号
     * @param command 更新コマンド
     * @return 更新された請求
     */
    Invoice updateInvoice(String invoiceNumber, UpdateInvoiceCommand command);

    /**
     * 全請求を取得する.
     *
     * @return 請求リスト
     */
    List<Invoice> getAllInvoices();

    /**
     * 請求番号で請求を取得する.
     *
     * @param invoiceNumber 請求番号
     * @return 請求
     */
    Invoice getInvoiceByNumber(String invoiceNumber);

    /**
     * ステータスで請求を検索する.
     *
     * @param status 請求ステータス
     * @return 請求リスト
     */
    List<Invoice> getInvoicesByStatus(InvoiceStatus status);

    /**
     * 顧客コードで請求を検索する.
     *
     * @param customerCode 顧客コード
     * @return 請求リスト
     */
    List<Invoice> getInvoicesByCustomer(String customerCode);

    /**
     * 請求を発行する.
     *
     * @param invoiceNumber 請求番号
     * @param version 楽観ロック用バージョン
     * @return 発行された請求
     */
    Invoice issueInvoice(String invoiceNumber, Integer version);

    /**
     * 請求を削除する.
     *
     * @param invoiceNumber 請求番号
     */
    void deleteInvoice(String invoiceNumber);
}
