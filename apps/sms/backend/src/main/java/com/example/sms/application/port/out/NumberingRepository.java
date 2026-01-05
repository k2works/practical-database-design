package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.NumberingHistory;
import com.example.sms.domain.model.common.NumberingMaster;

import java.util.List;
import java.util.Optional;

/**
 * 採番リポジトリ（Output Port）.
 */
public interface NumberingRepository {

    // NumberingMaster operations
    void saveMaster(NumberingMaster master);

    Optional<NumberingMaster> findMasterByNumberingCode(String numberingCode);

    List<NumberingMaster> findAllMasters();

    void updateMaster(NumberingMaster master);

    void deleteMasterByNumberingCode(String numberingCode);

    void deleteAllMasters();

    // NumberingHistory operations
    void saveHistory(NumberingHistory history);

    Optional<NumberingHistory> findHistoryById(Integer id);

    Optional<NumberingHistory> findHistoryByCodeAndYearMonth(String numberingCode, String yearMonth);

    List<NumberingHistory> findHistoriesByNumberingCode(String numberingCode);

    List<NumberingHistory> findAllHistories();

    void incrementLastNumber(Integer historyId);

    void updateHistory(NumberingHistory history);

    void deleteHistoryById(Integer id);

    void deleteHistoriesByNumberingCode(String numberingCode);

    void deleteAllHistories();
}
