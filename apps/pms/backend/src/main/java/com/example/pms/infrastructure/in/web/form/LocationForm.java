package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.location.Location;
import com.example.pms.domain.model.location.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 場所登録フォーム.
 */
@Data
public class LocationForm {

    @NotBlank(message = "場所コードは必須です")
    @Size(max = 20, message = "場所コードは20文字以内で入力してください")
    private String locationCode;

    @NotBlank(message = "場所名は必須です")
    @Size(max = 100, message = "場所名は100文字以内で入力してください")
    private String locationName;

    @NotNull(message = "場所区分は必須です")
    private LocationType locationType;

    @Size(max = 20, message = "親場所コードは20文字以内で入力してください")
    private String parentLocationCode;

    /**
     * フォームからエンティティを生成.
     *
     * @return 場所エンティティ
     */
    public Location toEntity() {
        return Location.builder()
            .locationCode(this.locationCode)
            .locationName(this.locationName)
            .locationType(this.locationType)
            .parentLocationCode(this.parentLocationCode)
            .build();
    }
}
