package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.inventory.Stocktaking;
import com.example.pms.domain.model.inventory.StocktakingDetail;
import com.example.pms.domain.model.inventory.StocktakingStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 棚卸フォーム.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StocktakingForm {

    private Integer id;

    private String stocktakingNumber;

    @NotBlank(message = "場所コードは必須です")
    private String locationCode;

    @NotNull(message = "棚卸日は必須です")
    private LocalDate stocktakingDate;

    @NotNull(message = "ステータスは必須です")
    private StocktakingStatus status;

    private Integer version;

    @Valid
    @Builder.Default
    private List<StocktakingDetailForm> details = new ArrayList<>();

    /**
     * 棚卸明細フォーム.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StocktakingDetailForm {

        private Integer id;

        @NotBlank(message = "品目は必須です")
        private String itemCode;

        private BigDecimal bookQuantity;

        @NotNull(message = "実棚数量は必須です")
        @Positive(message = "実棚数量は正の数で入力してください")
        private BigDecimal actualQuantity;

        /**
         * 明細フォームをエンティティに変換する.
         *
         * @param stocktakingNumber 棚卸番号
         * @param lineNumber 行番号
         * @return StocktakingDetail エンティティ
         */
        public StocktakingDetail toEntity(String stocktakingNumber, int lineNumber) {
            BigDecimal book = this.bookQuantity != null ? this.bookQuantity : BigDecimal.ZERO;
            BigDecimal actual = this.actualQuantity != null ? this.actualQuantity : BigDecimal.ZERO;
            BigDecimal difference = actual.subtract(book);

            return StocktakingDetail.builder()
                .id(this.id)
                .stocktakingNumber(stocktakingNumber)
                .lineNumber(lineNumber)
                .itemCode(this.itemCode)
                .bookQuantity(book)
                .actualQuantity(actual)
                .differenceQuantity(difference)
                .build();
        }

        /**
         * エンティティから明細フォームを作成する.
         *
         * @param detail StocktakingDetail エンティティ
         * @return StocktakingDetailForm
         */
        public static StocktakingDetailForm fromEntity(StocktakingDetail detail) {
            return StocktakingDetailForm.builder()
                .id(detail.getId())
                .itemCode(detail.getItemCode())
                .bookQuantity(detail.getBookQuantity())
                .actualQuantity(detail.getActualQuantity())
                .build();
        }
    }

    /**
     * フォームをエンティティに変換する.
     *
     * @return Stocktaking エンティティ
     */
    public Stocktaking toEntity() {
        List<StocktakingDetail> detailEntities = IntStream.range(0, details.size())
            .mapToObj(i -> details.get(i).toEntity(this.stocktakingNumber, i + 1))
            .toList();

        return Stocktaking.builder()
            .id(this.id)
            .stocktakingNumber(this.stocktakingNumber)
            .locationCode(this.locationCode)
            .stocktakingDate(this.stocktakingDate)
            .status(this.status)
            .version(this.version)
            .details(detailEntities)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param stocktaking Stocktaking エンティティ
     * @return StocktakingForm
     */
    public static StocktakingForm fromEntity(Stocktaking stocktaking) {
        List<StocktakingDetailForm> detailForms = new ArrayList<>();
        if (stocktaking.getDetails() != null) {
            detailForms = stocktaking.getDetails().stream()
                .map(StocktakingDetailForm::fromEntity)
                .toList();
        }

        return StocktakingForm.builder()
            .id(stocktaking.getId())
            .stocktakingNumber(stocktaking.getStocktakingNumber())
            .locationCode(stocktaking.getLocationCode())
            .stocktakingDate(stocktaking.getStocktakingDate())
            .status(stocktaking.getStatus())
            .version(stocktaking.getVersion())
            .details(new ArrayList<>(detailForms))
            .build();
    }
}
