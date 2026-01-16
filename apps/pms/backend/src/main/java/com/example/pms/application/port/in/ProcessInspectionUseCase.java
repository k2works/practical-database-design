package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.ProcessInspection;

import java.util.Optional;

/**
 * 工程検査（不良管理）ユースケース（Input Port）.
 */
public interface ProcessInspectionUseCase {

    /**
     * ページネーション付きで工程検査一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<ProcessInspection> getProcessInspectionList(int page, int size, String keyword);

    /**
     * 検査番号で工程検査を取得する.
     *
     * @param inspectionNumber 検査番号
     * @return 工程検査
     */
    Optional<ProcessInspection> getProcessInspection(String inspectionNumber);

    /**
     * 工程検査を登録する.
     *
     * @param inspection 工程検査
     * @return 登録した工程検査
     */
    ProcessInspection createProcessInspection(ProcessInspection inspection);

    /**
     * 工程検査を更新する.
     *
     * @param inspectionNumber 検査番号
     * @param inspection 工程検査
     * @return 更新した工程検査
     */
    ProcessInspection updateProcessInspection(String inspectionNumber, ProcessInspection inspection);

    /**
     * 工程検査を削除する.
     *
     * @param inspectionNumber 検査番号
     */
    void deleteProcessInspection(String inspectionNumber);
}
