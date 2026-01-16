package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.InspectionResult;

import java.util.List;
import java.util.Optional;

/**
 * 完成検査結果リポジトリ.
 */
public interface InspectionResultRepository {

    void save(InspectionResult inspectionResult);

    Optional<InspectionResult> findById(Integer id);

    Optional<InspectionResult> findByCompletionResultNumberAndDefectCode(
            String completionResultNumber, String defectCode);

    List<InspectionResult> findByCompletionResultNumber(String completionResultNumber);

    List<InspectionResult> findAll();

    /**
     * ページネーション付きで検索.
     *
     * @param offset オフセット
     * @param limit 件数
     * @param keyword キーワード（オプション）
     * @return 検査実績リスト
     */
    List<InspectionResult> findWithPagination(int offset, int limit, String keyword);

    /**
     * 件数をカウント.
     *
     * @param keyword キーワード（オプション）
     * @return 件数
     */
    long count(String keyword);

    /**
     * IDで削除.
     *
     * @param id ID
     */
    void deleteById(Integer id);

    void deleteAll();
}
