package com.example.pms.infrastructure.in.web.form;

import com.example.pms.application.port.in.command.CreateProcessCommand;
import com.example.pms.application.port.in.command.UpdateProcessCommand;
import com.example.pms.domain.model.process.Process;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 工程登録フォーム.
 */
@Data
public class ProcessForm {

    @NotBlank(message = "工程コードは必須です")
    @Size(max = 20, message = "工程コードは20文字以内で入力してください")
    private String processCode;

    @NotBlank(message = "工程名は必須です")
    @Size(max = 100, message = "工程名は100文字以内で入力してください")
    private String processName;

    @Size(max = 20, message = "工程区分は20文字以内で入力してください")
    private String processType;

    @Size(max = 20, message = "場所コードは20文字以内で入力してください")
    private String locationCode;

    /**
     * フォームを登録コマンドに変換する.
     *
     * @return 登録コマンド
     */
    public CreateProcessCommand toCreateCommand() {
        return CreateProcessCommand.builder()
            .processCode(this.processCode)
            .processName(this.processName)
            .processType(this.processType)
            .locationCode(this.locationCode)
            .build();
    }

    /**
     * フォームを更新コマンドに変換する.
     *
     * @return 更新コマンド
     */
    public UpdateProcessCommand toUpdateCommand() {
        return UpdateProcessCommand.builder()
            .processName(this.processName)
            .processType(this.processType)
            .locationCode(this.locationCode)
            .build();
    }

    /**
     * フォームからエンティティを生成.
     *
     * @return 工程エンティティ
     * @deprecated Command パターンを使用してください
     */
    @Deprecated
    public Process toEntity() {
        return Process.builder()
            .processCode(this.processCode)
            .processName(this.processName)
            .processType(this.processType)
            .locationCode(this.locationCode)
            .build();
    }
}
