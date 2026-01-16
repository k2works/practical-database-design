package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.InspectionResult;

import java.util.List;
import java.util.Optional;

/**
 * 検査実績ユースケース（Input Port）.
 */
public interface InspectionResultUseCase {

    /**
     * ページネーション付きで検査実績一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<InspectionResult> getInspectionResultList(int page, int size, String keyword);

    /**
     * 全検査実績を取得する.
     *
     * @return 検査実績リスト
     */
    List<InspectionResult> getAllInspectionResults();

    /**
     * IDで検査実績を取得する.
     *
     * @param id ID
     * @return 検査実績
     */
    Optional<InspectionResult> getInspectionResult(Integer id);

    /**
     * 検査実績を登録する.
     *
     * @param inspectionResult 検査実績
     * @return 登録した検査実績
     */
    InspectionResult createInspectionResult(InspectionResult inspectionResult);

    /**
     * 検査実績を更新する.
     *
     * @param id ID
     * @param inspectionResult 検査実績
     * @return 更新した検査実績
     */
    InspectionResult updateInspectionResult(Integer id, InspectionResult inspectionResult);

    /**
     * 検査実績を削除する.
     *
     * @param id ID
     */
    void deleteInspectionResult(Integer id);
}
