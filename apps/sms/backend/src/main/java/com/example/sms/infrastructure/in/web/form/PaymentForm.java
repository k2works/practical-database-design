package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreatePaymentCommand;
import com.example.sms.application.port.in.command.CreatePaymentCommand.CreatePaymentDetailCommand;
import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentDetail;
import com.example.sms.domain.model.payment.PaymentMethod;
import com.example.sms.domain.model.payment.PaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 支払登録・編集フォーム.
 */
@Data
public class PaymentForm {

    private String paymentNumber;

    @NotBlank(message = "仕入先コードは必須です")
    private String supplierCode;

    @NotNull(message = "支払締日は必須です")
    private LocalDate paymentClosingDate;

    @NotNull(message = "支払期日は必須です")
    private LocalDate paymentDueDate;

    @NotNull(message = "支払方法は必須です")
    private PaymentMethod paymentMethod;

    private BigDecimal paymentAmount;

    private BigDecimal taxAmount;

    private BigDecimal withholdingAmount;

    private BigDecimal netPaymentAmount;

    private String bankCode;

    private String branchCode;

    private String accountType;

    private String accountNumber;

    private String accountName;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    private PaymentStatus status;

    private Integer version;

    @Valid
    private List<PaymentDetailForm> details = new ArrayList<>();

    /**
     * デフォルトコンストラクタ.
     */
    public PaymentForm() {
        this.paymentClosingDate = LocalDate.now();
        this.paymentDueDate = LocalDate.now().plusDays(30);
        this.paymentMethod = PaymentMethod.BANK_TRANSFER;
        this.paymentAmount = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.withholdingAmount = BigDecimal.ZERO;
        this.netPaymentAmount = BigDecimal.ZERO;
        this.details.add(new PaymentDetailForm());
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreatePaymentCommand toCreateCommand() {
        List<CreatePaymentDetailCommand> detailCommands = this.details.stream()
            .filter(d -> d.getPurchaseNumber() != null && !d.getPurchaseNumber().isBlank())
            .map(d -> new CreatePaymentDetailCommand(
                d.getPurchaseNumber(),
                d.getPurchaseDate(),
                d.getPurchaseAmount(),
                d.getTaxAmount(),
                d.getPaymentTargetAmount()
            ))
            .toList();

        return new CreatePaymentCommand(
            this.supplierCode,
            this.paymentClosingDate,
            this.paymentDueDate,
            this.paymentMethod,
            this.bankCode,
            this.branchCode,
            this.accountType,
            this.accountNumber,
            this.accountName,
            this.remarks,
            detailCommands
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param payment 支払エンティティ
     * @return フォーム
     */
    public static PaymentForm from(Payment payment) {
        PaymentForm form = new PaymentForm();
        form.setPaymentNumber(payment.getPaymentNumber());
        form.setSupplierCode(payment.getSupplierCode());
        form.setPaymentClosingDate(payment.getPaymentClosingDate());
        form.setPaymentDueDate(payment.getPaymentDueDate());
        form.setPaymentMethod(payment.getPaymentMethod());
        form.setPaymentAmount(payment.getPaymentAmount());
        form.setTaxAmount(payment.getTaxAmount());
        form.setWithholdingAmount(payment.getWithholdingAmount());
        form.setNetPaymentAmount(payment.getNetPaymentAmount());
        form.setBankCode(payment.getBankCode());
        form.setBranchCode(payment.getBranchCode());
        form.setAccountType(payment.getAccountType());
        form.setAccountNumber(payment.getAccountNumber());
        form.setAccountName(payment.getAccountName());
        form.setRemarks(payment.getRemarks());
        form.setStatus(payment.getStatus());
        form.setVersion(payment.getVersion());

        List<PaymentDetailForm> detailForms = new ArrayList<>();
        if (payment.getDetails() != null) {
            for (PaymentDetail detail : payment.getDetails()) {
                detailForms.add(PaymentDetailForm.from(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new PaymentDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 支払明細フォーム.
     */
    @Data
    public static class PaymentDetailForm {

        private Integer id;

        private String purchaseNumber;

        private LocalDate purchaseDate;

        @PositiveOrZero(message = "仕入金額は0以上で入力してください")
        private BigDecimal purchaseAmount;

        @PositiveOrZero(message = "消費税額は0以上で入力してください")
        private BigDecimal taxAmount;

        @PositiveOrZero(message = "支払対象金額は0以上で入力してください")
        private BigDecimal paymentTargetAmount;

        /**
         * デフォルトコンストラクタ.
         */
        public PaymentDetailForm() {
            this.purchaseAmount = BigDecimal.ZERO;
            this.taxAmount = BigDecimal.ZERO;
            this.paymentTargetAmount = BigDecimal.ZERO;
        }

        /**
         * エンティティからフォームを生成.
         *
         * @param detail 支払明細エンティティ
         * @return フォーム
         */
        public static PaymentDetailForm from(PaymentDetail detail) {
            PaymentDetailForm form = new PaymentDetailForm();
            form.setId(detail.getId());
            form.setPurchaseNumber(detail.getPurchaseNumber());
            form.setPurchaseDate(detail.getPurchaseDate());
            form.setPurchaseAmount(detail.getPurchaseAmount());
            form.setTaxAmount(detail.getTaxAmount());
            form.setPaymentTargetAmount(detail.getPaymentTargetAmount());
            return form;
        }
    }
}
