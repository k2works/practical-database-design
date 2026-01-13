package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.location.Location;

import java.util.List;
import java.util.Optional;

/**
 * 場所ユースケースインターフェース（Input Port）.
 */
public interface LocationUseCase {

    /**
     * 場所一覧をページネーション付きで取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード（場所コードまたは場所名）
     * @return ページ結果
     */
    PageResult<Location> getLocations(int page, int size, String keyword);

    /**
     * すべての場所を取得する.
     *
     * @return 場所リスト
     */
    List<Location> getAllLocations();

    /**
     * 場所を登録する.
     *
     * @param location 場所
     * @return 登録した場所
     */
    Location createLocation(Location location);

    /**
     * 場所を取得する.
     *
     * @param locationCode 場所コード
     * @return 場所
     */
    Optional<Location> getLocation(String locationCode);

    /**
     * 場所を更新する.
     *
     * @param locationCode 場所コード
     * @param location 場所
     * @return 更新した場所
     */
    Location updateLocation(String locationCode, Location location);

    /**
     * 場所を削除する.
     *
     * @param locationCode 場所コード
     */
    void deleteLocation(String locationCode);
}
