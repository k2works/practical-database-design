package com.example.pms.application.port.out;

import com.example.pms.domain.model.quality.ShipmentInspection;

import java.util.List;
import java.util.Optional;

/**
 * 出荷検査リポジトリインターフェース.
 */
public interface ShipmentInspectionRepository {
    void save(ShipmentInspection inspection);

    /**
     * ページネーション付きで検索する.
     *
     * @param offset オフセット
     * @param limit リミット
     * @param keyword キーワード（オプション）
     * @return 出荷検査実績リスト
     */
    List<ShipmentInspection> findWithPagination(int offset, int limit, String keyword);

    /**
     * 件数をカウントする.
     *
     * @param keyword キーワード（オプション）
     * @return 件数
     */
    long count(String keyword);

    Optional<ShipmentInspection> findById(Integer id);

    Optional<ShipmentInspection> findByInspectionNumber(String inspectionNumber);

    Optional<ShipmentInspection> findByInspectionNumberWithResults(String inspectionNumber);

    List<ShipmentInspection> findByShipmentNumber(String shipmentNumber);

    List<ShipmentInspection> findAll();

    int update(ShipmentInspection inspection);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteAll();
}
