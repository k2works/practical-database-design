package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.Process;

import java.util.List;
import java.util.Optional;

/**
 * 工程マスタリポジトリインターフェース.
 */
public interface ProcessRepository {
    void save(Process process);
    Optional<Process> findByProcessCode(String processCode);
    List<Process> findAll();
    void update(Process process);
    void deleteAll();

    /**
     * ページネーション付きで工程を取得.
     *
     * @param keyword 検索キーワード（null可）
     * @param limit 取得件数
     * @param offset オフセット
     * @return 工程リスト
     */
    List<Process> findWithPagination(String keyword, int limit, int offset);

    /**
     * 条件に一致する工程の件数を取得.
     *
     * @param keyword 検索キーワード（null可）
     * @return 件数
     */
    long count(String keyword);

    /**
     * 工程コードで削除.
     *
     * @param processCode 工程コード
     */
    void deleteByProcessCode(String processCode);
}
