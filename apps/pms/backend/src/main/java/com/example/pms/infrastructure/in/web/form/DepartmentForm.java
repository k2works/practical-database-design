package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.department.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 部門登録フォーム.
 */
@Data
public class DepartmentForm {

    @NotBlank(message = "部門コードは必須です")
    @Size(max = 20, message = "部門コードは20文字以内で入力してください")
    private String departmentCode;

    @NotBlank(message = "部門名は必須です")
    @Size(max = 100, message = "部門名は100文字以内で入力してください")
    private String departmentName;

    @Size(max = 200, message = "部門パスは200文字以内で入力してください")
    private String departmentPath;

    private Boolean lowestLevel;

    @NotNull(message = "有効開始日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validTo;

    /**
     * フォームからエンティティを生成.
     *
     * @return 部門エンティティ
     */
    public Department toEntity() {
        return Department.builder()
            .departmentCode(this.departmentCode)
            .departmentName(this.departmentName)
            .departmentPath(this.departmentPath != null ? this.departmentPath : "/" + this.departmentCode)
            .lowestLevel(this.lowestLevel == null || this.lowestLevel)
            .validFrom(this.validFrom)
            .validTo(this.validTo != null ? this.validTo : LocalDate.of(9999, 12, 31))
            .build();
    }
}
