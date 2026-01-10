package com.example.fas.infrastructure.in.web.form;

import com.example.fas.application.port.in.command.CreateDepartmentCommand;
import com.example.fas.application.port.in.command.UpdateDepartmentCommand;
import com.example.fas.application.port.in.dto.DepartmentResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部門登録・編集フォーム.
 */
@Data
public class DepartmentForm {

    @NotBlank(message = "部門コードは必須です")
    @Size(max = 5, message = "部門コードは5文字以内で入力してください")
    private String departmentCode;

    @NotBlank(message = "部門名は必須です")
    @Size(max = 40, message = "部門名は40文字以内で入力してください")
    private String departmentName;

    @Size(max = 10, message = "部門略名は10文字以内で入力してください")
    private String departmentShortName;

    @NotNull(message = "組織階層は必須です")
    @Min(value = 0, message = "組織階層は0以上で入力してください")
    @Max(value = 9, message = "組織階層は9以下で入力してください")
    private Integer organizationLevel;

    @Size(max = 100, message = "部門パスは100文字以内で入力してください")
    private String departmentPath;

    @NotNull(message = "最下層フラグは必須です")
    @Min(value = 0, message = "最下層フラグは0または1で入力してください")
    @Max(value = 1, message = "最下層フラグは0または1で入力してください")
    private Integer lowestLevelFlag;

    /**
     * デフォルトコンストラクタ.
     */
    public DepartmentForm() {
        this.organizationLevel = 0;
        this.lowestLevelFlag = 0;
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateDepartmentCommand toCreateCommand() {
        return CreateDepartmentCommand.builder()
                .departmentCode(this.departmentCode)
                .departmentName(this.departmentName)
                .departmentShortName(this.departmentShortName)
                .organizationLevel(this.organizationLevel)
                .departmentPath(this.departmentPath)
                .lowestLevelFlag(this.lowestLevelFlag)
                .build();
    }

    /**
     * フォームを更新コマンドに変換.
     *
     * @return 更新コマンド
     */
    public UpdateDepartmentCommand toUpdateCommand() {
        return UpdateDepartmentCommand.builder()
                .departmentName(this.departmentName)
                .departmentShortName(this.departmentShortName)
                .organizationLevel(this.organizationLevel)
                .departmentPath(this.departmentPath)
                .lowestLevelFlag(this.lowestLevelFlag)
                .build();
    }

    /**
     * DepartmentResponse からフォームを生成.
     *
     * @param response 部門レスポンス
     * @return フォーム
     */
    public static DepartmentForm from(DepartmentResponse response) {
        DepartmentForm form = new DepartmentForm();
        form.setDepartmentCode(response.getDepartmentCode());
        form.setDepartmentName(response.getDepartmentName());
        form.setDepartmentShortName(response.getDepartmentShortName());
        form.setOrganizationLevel(response.getOrganizationLevel());
        form.setDepartmentPath(response.getDepartmentPath());
        form.setLowestLevelFlag(response.isLowestLevel() ? 1 : 0);
        return form;
    }
}
