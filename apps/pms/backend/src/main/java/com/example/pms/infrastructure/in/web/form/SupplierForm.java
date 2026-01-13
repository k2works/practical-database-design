package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.supplier.Supplier;
import com.example.pms.domain.model.supplier.SupplierType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 取引先登録フォーム.
 */
@Data
public class SupplierForm {

    @NotBlank(message = "取引先コードは必須です")
    @Size(max = 20, message = "取引先コードは20文字以内で入力してください")
    private String supplierCode;

    @NotNull(message = "適用開始日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveTo;

    @NotBlank(message = "取引先名は必須です")
    @Size(max = 100, message = "取引先名は100文字以内で入力してください")
    private String supplierName;

    @Size(max = 100, message = "取引先カナは100文字以内で入力してください")
    private String supplierNameKana;

    @NotNull(message = "取引先区分は必須です")
    private SupplierType supplierType;

    @Size(max = 8, message = "郵便番号は8文字以内で入力してください")
    private String postalCode;

    @Size(max = 200, message = "住所は200文字以内で入力してください")
    private String address;

    @Size(max = 20, message = "電話番号は20文字以内で入力してください")
    private String phoneNumber;

    @Size(max = 20, message = "FAX番号は20文字以内で入力してください")
    private String faxNumber;

    @Size(max = 50, message = "担当者名は50文字以内で入力してください")
    private String contactPerson;

    /**
     * フォームからエンティティを生成.
     *
     * @return 取引先エンティティ
     */
    public Supplier toEntity() {
        return Supplier.builder()
            .supplierCode(this.supplierCode)
            .effectiveFrom(this.effectiveFrom)
            .effectiveTo(this.effectiveTo != null ? this.effectiveTo : LocalDate.of(9999, 12, 31))
            .supplierName(this.supplierName)
            .supplierNameKana(this.supplierNameKana)
            .supplierType(this.supplierType)
            .postalCode(this.postalCode)
            .address(this.address)
            .phoneNumber(this.phoneNumber)
            .faxNumber(this.faxNumber)
            .contactPerson(this.contactPerson)
            .build();
    }
}
