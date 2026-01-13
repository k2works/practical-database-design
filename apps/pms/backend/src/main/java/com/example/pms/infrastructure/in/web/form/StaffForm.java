package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.staff.Staff;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 担当者登録フォーム.
 */
@Data
public class StaffForm {

    @NotBlank(message = "担当者コードは必須です")
    @Size(max = 20, message = "担当者コードは20文字以内で入力してください")
    private String staffCode;

    @NotBlank(message = "担当者名は必須です")
    @Size(max = 100, message = "担当者名は100文字以内で入力してください")
    private String staffName;

    @NotNull(message = "適用開始日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveTo;

    @Size(max = 20, message = "部門コードは20文字以内で入力してください")
    private String departmentCode;

    @Email(message = "メールアドレスの形式が正しくありません")
    @Size(max = 100, message = "メールアドレスは100文字以内で入力してください")
    private String email;

    @Size(max = 20, message = "電話番号は20文字以内で入力してください")
    private String phoneNumber;

    /**
     * フォームからエンティティを生成.
     *
     * @return 担当者エンティティ
     */
    public Staff toEntity() {
        return Staff.builder()
            .staffCode(this.staffCode)
            .staffName(this.staffName)
            .effectiveFrom(this.effectiveFrom)
            .effectiveTo(this.effectiveTo != null ? this.effectiveTo : LocalDate.of(9999, 12, 31))
            .departmentCode(this.departmentCode)
            .email(this.email)
            .phoneNumber(this.phoneNumber)
            .build();
    }
}
