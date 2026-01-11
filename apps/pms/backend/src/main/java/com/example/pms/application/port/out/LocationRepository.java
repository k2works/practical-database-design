package com.example.pms.application.port.out;

import com.example.pms.domain.model.location.Location;
import com.example.pms.domain.model.location.LocationType;

import java.util.List;
import java.util.Optional;

/**
 * 場所マスタリポジトリインターフェース.
 */
public interface LocationRepository {

    /**
     * 場所を保存する.
     *
     * @param location 場所
     */
    void save(Location location);

    /**
     * 場所コードで検索する.
     *
     * @param locationCode 場所コード
     * @return 場所
     */
    Optional<Location> findByLocationCode(String locationCode);

    /**
     * 場所区分で検索する.
     *
     * @param locationType 場所区分
     * @return 場所リスト
     */
    List<Location> findByLocationType(LocationType locationType);

    /**
     * 全件取得する.
     *
     * @return 場所リスト
     */
    List<Location> findAll();

    /**
     * 場所を更新する.
     *
     * @param location 場所
     */
    void update(Location location);

    /**
     * 全件削除する.
     */
    void deleteAll();
}
