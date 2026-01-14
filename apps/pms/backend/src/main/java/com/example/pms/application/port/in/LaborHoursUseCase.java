package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.LaborHours;

import java.util.List;
import java.util.Optional;

/**
 * 工数実績ユースケース（Input Port）.
 */
public interface LaborHoursUseCase {

    /**
     * ページネーション付きで工数実績一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword キーワード（オプション）
     * @return ページ結果
     */
    PageResult<LaborHours> getLaborHoursList(int page, int size, String keyword);

    /**
     * 全工数実績を取得する.
     *
     * @return 工数実績リスト
     */
    List<LaborHours> getAllLaborHours();

    /**
     * 工数実績番号で工数実績を取得する.
     *
     * @param laborHoursNumber 工数実績番号
     * @return 工数実績
     */
    Optional<LaborHours> getLaborHours(String laborHoursNumber);

    /**
     * 作業指示番号で工数実績を取得する.
     *
     * @param workOrderNumber 作業指示番号
     * @return 工数実績リスト
     */
    List<LaborHours> getLaborHoursByWorkOrder(String workOrderNumber);

    /**
     * 工数実績を登録する.
     *
     * @param laborHours 工数実績
     * @return 登録した工数実績
     */
    LaborHours createLaborHours(LaborHours laborHours);

    /**
     * 工数実績を更新する.
     *
     * @param laborHoursNumber 工数実績番号
     * @param laborHours 工数実績
     * @return 更新した工数実績
     */
    LaborHours updateLaborHours(String laborHoursNumber, LaborHours laborHours);

    /**
     * 工数実績を削除する.
     *
     * @param laborHoursNumber 工数実績番号
     */
    void deleteLaborHours(String laborHoursNumber);
}
