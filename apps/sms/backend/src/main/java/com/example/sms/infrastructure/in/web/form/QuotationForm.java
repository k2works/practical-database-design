package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreateQuotationCommand;
import com.example.sms.application.port.in.command.CreateQuotationCommand.CreateQuotationDetailCommand;
import com.example.sms.application.port.in.command.UpdateQuotationCommand;
import com.example.sms.domain.model.sales.Quotation;
import com.example.sms.domain.model.sales.QuotationDetail;
import com.example.sms.domain.model.sales.QuotationStatus;
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
 * 見積登録・編集フォーム.
 */
@Data
public class QuotationForm {

    private String quotationNumber;

    @NotNull(message = "見積日は必須です")
    private LocalDate quotationDate;

    private LocalDate validUntil;

    @NotBlank(message = "顧客コードは必須です")
    private String customerCode;

    private String customerBranchNumber;

    private String salesRepCode;

    @Size(max = 100, message = "件名は100文字以内で入力してください")
    private String subject;

    private BigDecimal subtotal;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private QuotationStatus status;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    private Integer version;

    @Valid
    private List<QuotationDetailForm> details = new ArrayList<>();

    /**
     * デフォルトコンストラクタ.
     */
    public QuotationForm() {
        this.quotationDate = LocalDate.now();
        this.validUntil = LocalDate.now().plusMonths(1);
        this.customerBranchNumber = "00";
        this.status = QuotationStatus.NEGOTIATING;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.details.add(new QuotationDetailForm());
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateQuotationCommand toCreateCommand() {
        List<CreateQuotationDetailCommand> detailCommands = this.details.stream()
            .filter(d -> d.getProductCode() != null && !d.getProductCode().isBlank())
            .map(d -> new CreateQuotationDetailCommand(
                d.getProductCode(),
                d.getProductName(),
                d.getQuantity(),
                d.getUnit(),
                d.getUnitPrice(),
                d.getRemarks()
            ))
            .toList();

        return new CreateQuotationCommand(
            this.quotationDate,
            this.validUntil,
            this.customerCode,
            this.customerBranchNumber,
            this.salesRepCode,
            this.subject,
            this.remarks,
            detailCommands
        );
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateQuotationCommand toUpdateCommand() {
        return new UpdateQuotationCommand(
            this.validUntil,
            this.subject,
            this.status,
            this.remarks,
            this.version
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param quotation 見積エンティティ
     * @return フォーム
     */
    public static QuotationForm from(Quotation quotation) {
        QuotationForm form = new QuotationForm();
        form.setQuotationNumber(quotation.getQuotationNumber());
        form.setQuotationDate(quotation.getQuotationDate());
        form.setValidUntil(quotation.getValidUntil());
        form.setCustomerCode(quotation.getCustomerCode());
        form.setCustomerBranchNumber(quotation.getCustomerBranchNumber());
        form.setSalesRepCode(quotation.getSalesRepCode());
        form.setSubject(quotation.getSubject());
        form.setSubtotal(quotation.getSubtotal());
        form.setTaxAmount(quotation.getTaxAmount());
        form.setTotalAmount(quotation.getTotalAmount());
        form.setStatus(quotation.getStatus());
        form.setRemarks(quotation.getRemarks());
        form.setVersion(quotation.getVersion());

        List<QuotationDetailForm> detailForms = new ArrayList<>();
        if (quotation.getDetails() != null) {
            for (QuotationDetail detail : quotation.getDetails()) {
                detailForms.add(QuotationDetailForm.from(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new QuotationDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 見積明細フォーム.
     */
    @Data
    public static class QuotationDetailForm {

        private Integer id;

        private String productCode;

        private String productName;

        @PositiveOrZero(message = "数量は0以上で入力してください")
        private BigDecimal quantity;

        private String unit;

        @PositiveOrZero(message = "単価は0以上で入力してください")
        private BigDecimal unitPrice;

        private BigDecimal amount;

        @Size(max = 200, message = "備考は200文字以内で入力してください")
        private String remarks;

        /**
         * デフォルトコンストラクタ.
         */
        public QuotationDetailForm() {
            this.quantity = BigDecimal.ONE;
            this.unitPrice = BigDecimal.ZERO;
            this.amount = BigDecimal.ZERO;
            this.unit = "個";
        }

        /**
         * エンティティからフォームを生成.
         *
         * @param detail 見積明細エンティティ
         * @return フォーム
         */
        public static QuotationDetailForm from(QuotationDetail detail) {
            QuotationDetailForm form = new QuotationDetailForm();
            form.setId(detail.getId());
            form.setProductCode(detail.getProductCode());
            form.setProductName(detail.getProductName());
            form.setQuantity(detail.getQuantity());
            form.setUnit(detail.getUnit());
            form.setUnitPrice(detail.getUnitPrice());
            form.setAmount(detail.getAmount());
            form.setRemarks(detail.getRemarks());
            return form;
        }
    }
}
