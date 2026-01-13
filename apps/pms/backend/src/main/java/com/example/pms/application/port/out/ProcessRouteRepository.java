package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.ProcessRoute;

import java.util.List;
import java.util.Optional;

/**
 * 工程表リポジトリインターフェース.
 */
public interface ProcessRouteRepository {
    void save(ProcessRoute processRoute);
    Optional<ProcessRoute> findByItemCodeAndSequence(String itemCode, Integer sequence);
    List<ProcessRoute> findByItemCode(String itemCode);
    List<ProcessRoute> findAll();
    void update(ProcessRoute processRoute);
    void deleteByItemCode(String itemCode);
    void deleteAll();

    /**
     * ページネーション付きで工程表を取得.
     *
     * @param itemCode 品目コード（null可）
     * @param limit 取得件数
     * @param offset オフセット
     * @return 工程表リスト
     */
    List<ProcessRoute> findWithPagination(String itemCode, int limit, int offset);

    /**
     * 条件に一致する工程表の件数を取得.
     *
     * @param itemCode 品目コード（null可）
     * @return 件数
     */
    long count(String itemCode);

    /**
     * 品目コードと工順で工程表を削除.
     *
     * @param itemCode 品目コード
     * @param sequence 工順
     */
    void deleteByItemCodeAndSequence(String itemCode, Integer sequence);
}
