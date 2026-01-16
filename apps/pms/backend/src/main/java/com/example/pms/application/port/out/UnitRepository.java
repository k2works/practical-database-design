package com.example.pms.application.port.out;

import com.example.pms.domain.model.unit.Unit;

import java.util.List;
import java.util.Optional;

/**
 * 単位リポジトリインターフェース.
 */
public interface UnitRepository {

    /**
     * 単位を保存する.
     *
     * @param unit 単位
     */
    void save(Unit unit);

    /**
     * 単位コードで検索する.
     *
     * @param unitCode 単位コード
     * @return 単位
     */
    Optional<Unit> findByUnitCode(String unitCode);

    /**
     * 全件取得する.
     *
     * @return 単位リスト
     */
    List<Unit> findAll();

    /**
     * ページネーション付きで取得する.
     *
     * @param keyword 検索キーワード
     * @param limit 取得件数
     * @param offset オフセット
     * @return 単位リスト
     */
    List<Unit> findWithPagination(String keyword, int limit, int offset);

    /**
     * 件数を取得する.
     *
     * @param keyword 検索キーワード
     * @return 件数
     */
    long count(String keyword);

    /**
     * 単位を更新する.
     *
     * @param unit 単位
     */
    void update(Unit unit);

    /**
     * 単位コードで削除する.
     *
     * @param unitCode 単位コード
     */
    void deleteByUnitCode(String unitCode);

    /**
     * 全件削除する.
     */
    void deleteAll();
}
