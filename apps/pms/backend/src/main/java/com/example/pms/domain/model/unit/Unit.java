package com.example.pms.domain.model.unit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 単位マスタ.
 * 数量の単位を管理するドメインモデル。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.ShortClassName")
public class Unit {

    /**
     * 単位コード.
     * 単位を一意に識別するコード（例: PCS, KG, M）
     */
    private String unitCode;

    /**
     * 単位記号.
     * 単位の記号（個、kg、m 等）
     */
    private String unitSymbol;

    /**
     * 単位名.
     * 単位の名称
     */
    private String unitName;

    /**
     * 作成日時.
     */
    private LocalDateTime createdAt;

    /**
     * 更新日時.
     */
    private LocalDateTime updatedAt;
}
