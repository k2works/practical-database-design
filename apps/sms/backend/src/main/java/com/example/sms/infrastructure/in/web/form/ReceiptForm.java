package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreateReceiptCommand;
import com.example.sms.application.port.in.command.UpdateReceiptCommand;
import com.example.sms.domain.model.receipt.Receipt;
import com.example.sms.domain.model.receipt.ReceiptMethod;
import com.example.sms.domain.model.receipt.ReceiptStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入金登録・編集フォーム.
 */
@Data
public class ReceiptForm {

    private String receiptNumber;

    @NotNull(message = "入金日は必須です")
    private LocalDate receiptDate;

    @NotBlank(message = "顧客コードは必須です")
    private String customerCode;

    private String customerBranchNumber;

    @NotNull(message = "入金方法は必須です")
    private ReceiptMethod receiptMethod;

    @NotNull(message = "入金額は必須です")
    private BigDecimal receiptAmount;

    private BigDecimal appliedAmount;

    private BigDecimal unappliedAmount;

    private BigDecimal bankFee;

    private String payerName;

    private String bankName;

    private String accountNumber;

    private ReceiptStatus status;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    private Integer version;

    /**
     * デフォルトコンストラクタ.
     */
    public ReceiptForm() {
        this.receiptDate = LocalDate.now();
        this.customerBranchNumber = "00";
        this.receiptMethod = ReceiptMethod.BANK_TRANSFER;
        this.receiptAmount = BigDecimal.ZERO;
        this.appliedAmount = BigDecimal.ZERO;
        this.unappliedAmount = BigDecimal.ZERO;
        this.bankFee = BigDecimal.ZERO;
        this.status = ReceiptStatus.RECEIVED;
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateReceiptCommand toCreateCommand() {
        return new CreateReceiptCommand(
            this.receiptDate,
            this.customerCode,
            this.customerBranchNumber,
            this.receiptMethod,
            this.receiptAmount,
            this.bankFee,
            this.payerName,
            this.bankName,
            this.accountNumber,
            this.remarks
        );
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateReceiptCommand toUpdateCommand() {
        return new UpdateReceiptCommand(
            this.status,
            this.remarks,
            this.version
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param receipt 入金エンティティ
     * @return フォーム
     */
    public static ReceiptForm from(Receipt receipt) {
        ReceiptForm form = new ReceiptForm();
        form.setReceiptNumber(receipt.getReceiptNumber());
        form.setReceiptDate(receipt.getReceiptDate());
        form.setCustomerCode(receipt.getCustomerCode());
        form.setCustomerBranchNumber(receipt.getCustomerBranchNumber());
        form.setReceiptMethod(receipt.getReceiptMethod());
        form.setReceiptAmount(receipt.getReceiptAmount());
        form.setAppliedAmount(receipt.getAppliedAmount());
        form.setUnappliedAmount(receipt.getUnappliedAmount());
        form.setBankFee(receipt.getBankFee());
        form.setPayerName(receipt.getPayerName());
        form.setBankName(receipt.getBankName());
        form.setAccountNumber(receipt.getAccountNumber());
        form.setStatus(receipt.getStatus());
        form.setRemarks(receipt.getRemarks());
        form.setVersion(receipt.getVersion());
        return form;
    }
}
