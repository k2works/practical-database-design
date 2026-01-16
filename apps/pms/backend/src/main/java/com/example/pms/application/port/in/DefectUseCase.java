package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.defect.Defect;

import java.util.List;
import java.util.Optional;

/**
 * 欠点マスタユースケース（Input Port）.
 */
public interface DefectUseCase {

    /**
     * ページネーション付きで欠点一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<Defect> getDefectList(int page, int size, String keyword);

    /**
     * 全欠点を取得する.
     *
     * @return 欠点リスト
     */
    List<Defect> getAllDefects();

    /**
     * 欠点コードで欠点を取得する.
     *
     * @param defectCode 欠点コード
     * @return 欠点
     */
    Optional<Defect> getDefect(String defectCode);

    /**
     * 欠点を登録する.
     *
     * @param defect 欠点
     * @return 登録した欠点
     */
    Defect createDefect(Defect defect);

    /**
     * 欠点を更新する.
     *
     * @param defectCode 欠点コード
     * @param defect 欠点
     * @return 更新した欠点
     */
    Defect updateDefect(String defectCode, Defect defect);

    /**
     * 欠点を削除する.
     *
     * @param defectCode 欠点コード
     */
    void deleteDefect(String defectCode);
}
