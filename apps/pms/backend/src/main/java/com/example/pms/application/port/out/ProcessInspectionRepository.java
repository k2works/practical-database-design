package com.example.pms.application.port.out;

import com.example.pms.domain.model.quality.ProcessInspection;

import java.util.List;
import java.util.Optional;

/**
 * 工程検査リポジトリインターフェース.
 */
public interface ProcessInspectionRepository {
    void save(ProcessInspection inspection);

    /**
     * ページネーション付きで検索する.
     *
     * @param offset オフセット
     * @param limit リミット
     * @param keyword キーワード（オプション）
     * @return 工程検査リスト
     */
    List<ProcessInspection> findWithPagination(int offset, int limit, String keyword);

    /**
     * 件数をカウントする.
     *
     * @param keyword キーワード（オプション）
     * @return 件数
     */
    long count(String keyword);

    Optional<ProcessInspection> findById(Integer id);

    Optional<ProcessInspection> findByInspectionNumber(String inspectionNumber);

    Optional<ProcessInspection> findByInspectionNumberWithResults(String inspectionNumber);

    List<ProcessInspection> findByWorkOrderNumber(String workOrderNumber);

    List<ProcessInspection> findByProcessCode(String processCode);

    List<ProcessInspection> findAll();

    int update(ProcessInspection inspection);

    void deleteByInspectionNumber(String inspectionNumber);

    void deleteAll();
}
