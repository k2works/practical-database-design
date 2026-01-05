package com.example.sms.infrastructure.in.web.form;

import com.example.sms.application.port.in.command.CreateStocktakingCommand;
import com.example.sms.application.port.in.command.CreateStocktakingCommand.CreateStocktakingDetailCommand;
import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingDetail;
import com.example.sms.domain.model.inventory.StocktakingStatus;
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
 * 棚卸登録・編集フォーム.
 */
@Data
public class StocktakingForm {

    private String stocktakingNumber;

    @NotBlank(message = "倉庫コードは必須です")
    private String warehouseCode;

    @NotNull(message = "棚卸日は必須です")
    private LocalDate stocktakingDate;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String remarks;

    private StocktakingStatus status;

    private Integer version;

    @Valid
    private List<StocktakingDetailForm> details = new ArrayList<>();

    /**
     * デフォルトコンストラクタ.
     */
    public StocktakingForm() {
        this.stocktakingDate = LocalDate.now();
        this.details.add(new StocktakingDetailForm());
    }

    /**
     * フォームを登録コマンドに変換.
     *
     * @return 登録コマンド
     */
    public CreateStocktakingCommand toCreateCommand() {
        List<CreateStocktakingDetailCommand> detailCommands = this.details.stream()
            .filter(d -> d.getProductCode() != null && !d.getProductCode().isBlank())
            .map(d -> new CreateStocktakingDetailCommand(
                d.getProductCode(),
                d.getLocationCode(),
                d.getLotNumber(),
                d.getBookQuantity(),
                d.getActualQuantity(),
                d.getDifferenceReason()
            ))
            .toList();

        return new CreateStocktakingCommand(
            this.warehouseCode,
            this.stocktakingDate,
            this.remarks,
            detailCommands
        );
    }

    /**
     * エンティティからフォームを生成.
     *
     * @param stocktaking 棚卸エンティティ
     * @return フォーム
     */
    public static StocktakingForm from(Stocktaking stocktaking) {
        StocktakingForm form = new StocktakingForm();
        form.setStocktakingNumber(stocktaking.getStocktakingNumber());
        form.setWarehouseCode(stocktaking.getWarehouseCode());
        form.setStocktakingDate(stocktaking.getStocktakingDate());
        form.setRemarks(stocktaking.getRemarks());
        form.setStatus(stocktaking.getStatus());
        form.setVersion(stocktaking.getVersion());

        List<StocktakingDetailForm> detailForms = new ArrayList<>();
        if (stocktaking.getDetails() != null) {
            for (StocktakingDetail detail : stocktaking.getDetails()) {
                detailForms.add(StocktakingDetailForm.from(detail));
            }
        }
        if (detailForms.isEmpty()) {
            detailForms.add(new StocktakingDetailForm());
        }
        form.setDetails(detailForms);

        return form;
    }

    /**
     * 棚卸明細フォーム.
     */
    @Data
    public static class StocktakingDetailForm {

        private Integer id;

        private String productCode;

        private String locationCode;

        private String lotNumber;

        @PositiveOrZero(message = "帳簿数量は0以上で入力してください")
        private BigDecimal bookQuantity;

        @PositiveOrZero(message = "実棚数量は0以上で入力してください")
        private BigDecimal actualQuantity;

        private BigDecimal differenceQuantity;

        @Size(max = 200, message = "差異理由は200文字以内で入力してください")
        private String differenceReason;

        private Boolean adjustedFlag;

        /**
         * デフォルトコンストラクタ.
         */
        public StocktakingDetailForm() {
            this.bookQuantity = BigDecimal.ZERO;
            this.actualQuantity = BigDecimal.ZERO;
            this.differenceQuantity = BigDecimal.ZERO;
            this.adjustedFlag = false;
        }

        /**
         * エンティティからフォームを生成.
         *
         * @param detail 棚卸明細エンティティ
         * @return フォーム
         */
        public static StocktakingDetailForm from(StocktakingDetail detail) {
            StocktakingDetailForm form = new StocktakingDetailForm();
            form.setId(detail.getId());
            form.setProductCode(detail.getProductCode());
            form.setLocationCode(detail.getLocationCode());
            form.setLotNumber(detail.getLotNumber());
            form.setBookQuantity(detail.getBookQuantity());
            form.setActualQuantity(detail.getActualQuantity());
            form.setDifferenceQuantity(detail.getDifferenceQuantity());
            form.setDifferenceReason(detail.getDifferenceReason());
            form.setAdjustedFlag(detail.getAdjustedFlag());
            return form;
        }
    }
}
