package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.inventory.Stocktaking;
import com.example.pms.domain.model.inventory.StocktakingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    /**
     * フォームをエンティティに変換する.
     *
     * @return Stocktaking エンティティ
     */
    public Stocktaking toEntity() {
        return Stocktaking.builder()
            .id(this.id)
            .stocktakingNumber(this.stocktakingNumber)
            .locationCode(this.locationCode)
            .stocktakingDate(this.stocktakingDate)
            .status(this.status)
            .version(this.version)
            .build();
    }

    /**
     * エンティティからフォームを作成する.
     *
     * @param stocktaking Stocktaking エンティティ
     * @return StocktakingForm
     */
    public static StocktakingForm fromEntity(Stocktaking stocktaking) {
        return StocktakingForm.builder()
            .id(stocktaking.getId())
            .stocktakingNumber(stocktaking.getStocktakingNumber())
            .locationCode(stocktaking.getLocationCode())
            .stocktakingDate(stocktaking.getStocktakingDate())
            .status(stocktaking.getStatus())
            .version(stocktaking.getVersion())
            .build();
    }
}
