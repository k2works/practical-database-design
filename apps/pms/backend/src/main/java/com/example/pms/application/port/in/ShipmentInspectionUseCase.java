package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.ShipmentInspection;

import java.util.Optional;

/**
 * 出荷検査実績ユースケース（Input Port）.
 */
public interface ShipmentInspectionUseCase {

    /**
     * ページネーション付きで出荷検査実績一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<ShipmentInspection> getShipmentInspectionList(int page, int size, String keyword);

    /**
     * 検査番号で出荷検査実績を取得する.
     *
     * @param inspectionNumber 検査番号
     * @return 出荷検査実績
     */
    Optional<ShipmentInspection> getShipmentInspection(String inspectionNumber);

    /**
     * 出荷検査実績を登録する.
     *
     * @param inspection 出荷検査実績
     * @return 登録した出荷検査実績
     */
    ShipmentInspection createShipmentInspection(ShipmentInspection inspection);

    /**
     * 出荷検査実績を更新する.
     *
     * @param inspectionNumber 検査番号
     * @param inspection 出荷検査実績
     * @return 更新した出荷検査実績
     */
    ShipmentInspection updateShipmentInspection(String inspectionNumber, ShipmentInspection inspection);

    /**
     * 出荷検査実績を削除する.
     *
     * @param inspectionNumber 検査番号
     */
    void deleteShipmentInspection(String inspectionNumber);
}
