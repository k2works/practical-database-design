package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.LaborHours;

import java.util.List;
import java.util.Optional;

/**
 * 工数実績リポジトリ.
 */
public interface LaborHoursRepository {

    void save(LaborHours laborHours);

    Optional<LaborHours> findById(Integer id);

    Optional<LaborHours> findByLaborHoursNumber(String laborHoursNumber);

    List<LaborHours> findByWorkOrderNumber(String workOrderNumber);

    List<LaborHours> findByWorkOrderNumberAndSequence(String workOrderNumber, Integer sequence);

    List<LaborHours> findAll();

    /**
     * ページネーション付きで工数実績を取得する.
     *
     * @param offset オフセット
     * @param limit リミット
     * @param keyword キーワード
     * @return 工数実績リスト
     */
    List<LaborHours> findWithPagination(int offset, int limit, String keyword);

    /**
     * 工数実績の件数を取得する.
     *
     * @param keyword キーワード
     * @return 件数
     */
    long count(String keyword);

    /**
     * 工数実績を更新する.
     *
     * @param laborHours 工数実績
     */
    void update(LaborHours laborHours);

    /**
     * 工数実績番号で削除する.
     *
     * @param laborHoursNumber 工数実績番号
     */
    void deleteByLaborHoursNumber(String laborHoursNumber);

    void deleteAll();
}
