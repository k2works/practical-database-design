package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateQuotationCommand;
import com.example.sms.application.port.in.command.UpdateQuotationCommand;
import com.example.sms.domain.model.sales.Quotation;
import com.example.sms.domain.model.sales.QuotationStatus;

import java.util.List;

/**
 * 見積ユースケース（Input Port）.
 */
public interface QuotationUseCase {

    /**
     * 見積を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された見積
     */
    Quotation createQuotation(CreateQuotationCommand command);

    /**
     * 見積を更新する.
     *
     * @param quotationNumber 見積番号
     * @param command 更新コマンド
     * @return 更新された見積
     */
    Quotation updateQuotation(String quotationNumber, UpdateQuotationCommand command);

    /**
     * 全見積を取得する.
     *
     * @return 見積リスト
     */
    List<Quotation> getAllQuotations();

    /**
     * 見積番号で見積を取得する.
     *
     * @param quotationNumber 見積番号
     * @return 見積
     */
    Quotation getQuotationByNumber(String quotationNumber);

    /**
     * 見積番号で見積（明細含む）を取得する.
     *
     * @param quotationNumber 見積番号
     * @return 見積（明細含む）
     */
    Quotation getQuotationWithDetails(String quotationNumber);

    /**
     * ステータスで見積を検索する.
     *
     * @param status 見積ステータス
     * @return 見積リスト
     */
    List<Quotation> getQuotationsByStatus(QuotationStatus status);

    /**
     * 顧客コードで見積を検索する.
     *
     * @param customerCode 顧客コード
     * @return 見積リスト
     */
    List<Quotation> getQuotationsByCustomer(String customerCode);

    /**
     * 見積を削除する.
     *
     * @param quotationNumber 見積番号
     */
    void deleteQuotation(String quotationNumber);

    /**
     * 見積を受注確定にする.
     *
     * @param quotationNumber 見積番号
     * @param version 楽観ロック用バージョン
     * @return 更新された見積
     */
    Quotation confirmQuotation(String quotationNumber, Integer version);

    /**
     * 見積を失注にする.
     *
     * @param quotationNumber 見積番号
     * @param version 楽観ロック用バージョン
     * @return 更新された見積
     */
    Quotation loseQuotation(String quotationNumber, Integer version);
}
