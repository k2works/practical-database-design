package com.example.pms.infrastructure.in.web.form;

import com.example.pms.application.port.in.command.CreateProcessRouteCommand;
import com.example.pms.application.port.in.command.UpdateProcessRouteCommand;
import com.example.pms.domain.model.process.ProcessRoute;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 工程表登録フォーム.
 */
@Data
public class ProcessRouteForm {

    @NotBlank(message = "品目コードは必須です")
    @Size(max = 20, message = "品目コードは20文字以内で入力してください")
    private String itemCode;

    @NotNull(message = "工順は必須です")
    @Positive(message = "工順は正の整数で入力してください")
    private Integer sequence;

    @NotBlank(message = "工程コードは必須です")
    @Size(max = 20, message = "工程コードは20文字以内で入力してください")
    private String processCode;

    @PositiveOrZero(message = "標準作業時間は0以上で入力してください")
    private BigDecimal standardTime;

    @PositiveOrZero(message = "段取時間は0以上で入力してください")
    private BigDecimal setupTime;

    /**
     * フォームから登録コマンドを生成.
     *
     * @return 登録コマンド
     */
    public CreateProcessRouteCommand toCreateCommand() {
        return new CreateProcessRouteCommand(
            this.itemCode,
            this.sequence,
            this.processCode,
            this.standardTime,
            this.setupTime
        );
    }

    /**
     * フォームから更新コマンドを生成.
     *
     * @return 更新コマンド
     */
    public UpdateProcessRouteCommand toUpdateCommand() {
        return new UpdateProcessRouteCommand(
            this.processCode,
            this.standardTime,
            this.setupTime
        );
    }

    /**
     * フォームからエンティティを生成.
     *
     * @return 工程表エンティティ
     * @deprecated Use {@link #toCreateCommand()} or {@link #toUpdateCommand()} instead
     */
    @Deprecated
    public ProcessRoute toEntity() {
        return ProcessRoute.builder()
            .itemCode(this.itemCode)
            .sequence(this.sequence)
            .processCode(this.processCode)
            .standardTime(this.standardTime)
            .setupTime(this.setupTime)
            .build();
    }
}
