package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreateInvoiceCommand;
import com.example.sms.application.port.in.command.UpdateInvoiceCommand;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.invoice.InvoiceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 請求登録・編集フォーム.
 */
@Data
public class InvoiceForm {

    private String invoiceNumber;

    @NotNull(message = "請求日は必須です")
    private LocalDate invoiceDate;

    @NotBlank(message = "顧客コードは必須です")
    private String customerCode;

    private String customerBranchNumber;

    private LocalDate closingDate;

    private BigDecimal previousBalance;

    private BigDecimal receiptAmount;

    private BigDecimal currentSalesAmount;

    private BigDecimal currentTaxAmount;

    private BigDecimal currentInvoiceAmount;

    private BigDecimal invoiceBalance;

    private LocalDate dueDate;

    private InvoiceStatus status;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    private Integer version;

    /**
     * デフォルトコンストラクタ.
     */
    public InvoiceForm() {
        this.invoiceDate = LocalDate.now();
        this.closingDate = LocalDate.now();
        this.customerBranchNumber = "00";
        this.status = InvoiceStatus.DRAFT;
        this.previousBalance = BigDecimal.ZERO;
        this.receiptAmount = BigDecimal.ZERO;
        this.currentSalesAmount = BigDecimal.ZERO;
        this.currentTaxAmount = BigDecimal.ZERO;
        this.currentInvoiceAmount = BigDecimal.ZERO;
        this.invoiceBalance = BigDecimal.ZERO;
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateInvoiceCommand toCreateCommand() {
        return new CreateInvoiceCommand(
            this.invoiceDate,
            this.customerCode,
            this.customerBranchNumber,
            this.closingDate,
            this.previousBalance,
            this.receiptAmount,
            this.currentSalesAmount,
            this.currentTaxAmount,
            this.dueDate,
            this.remarks
        );
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateInvoiceCommand toUpdateCommand() {
        return new UpdateInvoiceCommand(
            this.status,
            this.remarks,
            this.version
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param invoice 請求エンティティ
     * @return フォーム
     */
    public static InvoiceForm from(Invoice invoice) {
        InvoiceForm form = new InvoiceForm();
        form.setInvoiceNumber(invoice.getInvoiceNumber());
        form.setInvoiceDate(invoice.getInvoiceDate());
        form.setCustomerCode(invoice.getCustomerCode());
        form.setCustomerBranchNumber(invoice.getCustomerBranchNumber());
        form.setClosingDate(invoice.getClosingDate());
        form.setPreviousBalance(invoice.getPreviousBalance());
        form.setReceiptAmount(invoice.getReceiptAmount());
        form.setCurrentSalesAmount(invoice.getCurrentSalesAmount());
        form.setCurrentTaxAmount(invoice.getCurrentTaxAmount());
        form.setCurrentInvoiceAmount(invoice.getCurrentInvoiceAmount());
        form.setInvoiceBalance(invoice.getInvoiceBalance());
        form.setDueDate(invoice.getDueDate());
        form.setStatus(invoice.getStatus());
        form.setRemarks(invoice.getRemarks());
        form.setVersion(invoice.getVersion());
        return form;
    }
}
