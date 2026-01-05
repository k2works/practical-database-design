package com.example.sms.application.port.in;

import com.example.sms.infrastructure.in.web.dto.InventoryReportData;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.sales.Quotation;

import java.util.List;

/**
 * 帳票ユースケース（Input Port）.
 */
public interface ReportUseCase {

    /**
     * 在庫レポートデータを取得する.
     *
     * @return 在庫レポートデータリスト
     */
    List<InventoryReportData> getInventoryReport();

    /**
     * 請求書データを取得する.
     *
     * @param invoiceNumber 請求番号
     * @return 請求データ
     */
    Invoice getInvoiceForReport(String invoiceNumber);

    /**
     * 発注書データを取得する.
     *
     * @param purchaseOrderNumber 発注番号
     * @return 発注データ
     */
    PurchaseOrder getPurchaseOrderForReport(String purchaseOrderNumber);

    /**
     * 見積書データを取得する.
     *
     * @param quotationNumber 見積番号
     * @return 見積データ
     */
    Quotation getQuotationForReport(String quotationNumber);
}
