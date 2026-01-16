package com.example.pms.infrastructure.in.web.form;

import com.example.pms.application.port.in.command.CreateUnitCommand;
import com.example.pms.application.port.in.command.UpdateUnitCommand;
import com.example.pms.domain.model.unit.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 単位登録フォーム.
 */
@Data
public class UnitForm {

    @NotBlank(message = "単位コードは必須です")
    @Size(max = 10, message = "単位コードは10文字以内で入力してください")
    private String unitCode;

    @NotBlank(message = "単位記号は必須です")
    @Size(max = 10, message = "単位記号は10文字以内で入力してください")
    private String unitSymbol;

    @NotBlank(message = "単位名は必須です")
    @Size(max = 50, message = "単位名は50文字以内で入力してください")
    private String unitName;

    /**
     * フォームを登録コマンドに変換する.
     *
     * @return 登録コマンド
     */
    public CreateUnitCommand toCreateCommand() {
        return CreateUnitCommand.builder()
            .unitCode(this.unitCode)
            .unitSymbol(this.unitSymbol)
            .unitName(this.unitName)
            .build();
    }

    /**
     * フォームを更新コマンドに変換する.
     *
     * @return 更新コマンド
     */
    public UpdateUnitCommand toUpdateCommand() {
        return UpdateUnitCommand.builder()
            .unitSymbol(this.unitSymbol)
            .unitName(this.unitName)
            .build();
    }

    /**
     * フォームからエンティティを生成.
     *
     * @return 単位エンティティ
     * @deprecated Command パターンを使用してください
     */
    @Deprecated
    public Unit toEntity() {
        return Unit.builder()
            .unitCode(this.unitCode)
            .unitSymbol(this.unitSymbol)
            .unitName(this.unitName)
            .build();
    }
}
