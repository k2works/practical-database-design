package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreatePartnerCommand;
import com.example.sms.application.port.in.command.UpdatePartnerCommand;
import com.example.sms.domain.model.partner.Partner;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 取引先登録・編集フォーム.
 */
@Data
public class PartnerForm {

    @NotBlank(message = "取引先コードは必須です")
    @Size(max = 20, message = "取引先コードは20文字以内で入力してください")
    private String partnerCode;

    @NotBlank(message = "取引先名は必須です")
    @Size(max = 100, message = "取引先名は100文字以内で入力してください")
    private String partnerName;

    @Size(max = 100, message = "取引先名カナは100文字以内で入力してください")
    private String partnerNameKana;

    private Boolean isCustomer;

    private Boolean isSupplier;

    @Size(max = 10, message = "郵便番号は10文字以内で入力してください")
    private String postalCode;

    @Size(max = 100, message = "住所1は100文字以内で入力してください")
    private String address1;

    @Size(max = 100, message = "住所2は100文字以内で入力してください")
    private String address2;

    private String classificationCode;

    private Boolean isTradingProhibited;

    private Boolean isMiscellaneous;

    private String groupCode;

    @PositiveOrZero(message = "与信限度額は0以上で入力してください")
    private BigDecimal creditLimit;

    @PositiveOrZero(message = "一時与信増額は0以上で入力してください")
    private BigDecimal temporaryCreditIncrease;

    /**
     * デフォルトコンストラクタ.
     */
    public PartnerForm() {
        this.isCustomer = false;
        this.isSupplier = false;
        this.isTradingProhibited = false;
        this.isMiscellaneous = false;
        this.creditLimit = BigDecimal.ZERO;
        this.temporaryCreditIncrease = BigDecimal.ZERO;
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreatePartnerCommand toCreateCommand() {
        return new CreatePartnerCommand(
            this.partnerCode,
            this.partnerName,
            this.partnerNameKana,
            Boolean.TRUE.equals(this.isCustomer),
            Boolean.TRUE.equals(this.isSupplier),
            this.postalCode,
            this.address1,
            this.address2,
            this.classificationCode,
            Boolean.TRUE.equals(this.isTradingProhibited),
            Boolean.TRUE.equals(this.isMiscellaneous),
            this.groupCode,
            this.creditLimit,
            this.temporaryCreditIncrease
        );
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdatePartnerCommand toUpdateCommand() {
        return new UpdatePartnerCommand(
            this.partnerName,
            this.partnerNameKana,
            this.isCustomer,
            this.isSupplier,
            this.postalCode,
            this.address1,
            this.address2,
            this.classificationCode,
            this.isTradingProhibited,
            this.isMiscellaneous,
            this.groupCode,
            this.creditLimit,
            this.temporaryCreditIncrease
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param partner 取引先エンティティ
     * @return フォーム
     */
    public static PartnerForm from(Partner partner) {
        PartnerForm form = new PartnerForm();
        form.setPartnerCode(partner.getPartnerCode());
        form.setPartnerName(partner.getPartnerName());
        form.setPartnerNameKana(partner.getPartnerNameKana());
        form.setIsCustomer(partner.isCustomer());
        form.setIsSupplier(partner.isSupplier());
        form.setPostalCode(partner.getPostalCode());
        form.setAddress1(partner.getAddress1());
        form.setAddress2(partner.getAddress2());
        form.setClassificationCode(partner.getClassificationCode());
        form.setIsTradingProhibited(partner.isTradingProhibited());
        form.setIsMiscellaneous(partner.isMiscellaneous());
        form.setGroupCode(partner.getGroupCode());
        form.setCreditLimit(partner.getCreditLimit());
        form.setTemporaryCreditIncrease(partner.getTemporaryCreditIncrease());
        return form;
    }
}
