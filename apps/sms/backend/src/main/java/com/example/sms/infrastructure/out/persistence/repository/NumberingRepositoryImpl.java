package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.NumberingRepository;
import com.example.sms.domain.model.common.NumberingHistory;
import com.example.sms.domain.model.common.NumberingMaster;
import com.example.sms.infrastructure.out.persistence.mapper.NumberingHistoryMapper;
import com.example.sms.infrastructure.out.persistence.mapper.NumberingMasterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 採番リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class NumberingRepositoryImpl implements NumberingRepository {

    private final NumberingMasterMapper numberingMasterMapper;
    private final NumberingHistoryMapper numberingHistoryMapper;

    // NumberingMaster operations

    @Override
    public void saveMaster(NumberingMaster master) {
        numberingMasterMapper.insert(master);
    }

    @Override
    public Optional<NumberingMaster> findMasterByNumberingCode(String numberingCode) {
        return numberingMasterMapper.findByNumberingCode(numberingCode);
    }

    @Override
    public List<NumberingMaster> findAllMasters() {
        return numberingMasterMapper.findAll();
    }

    @Override
    public void updateMaster(NumberingMaster master) {
        numberingMasterMapper.update(master);
    }

    @Override
    public void deleteMasterByNumberingCode(String numberingCode) {
        numberingMasterMapper.deleteByNumberingCode(numberingCode);
    }

    @Override
    public void deleteAllMasters() {
        numberingMasterMapper.deleteAll();
    }

    // NumberingHistory operations

    @Override
    public void saveHistory(NumberingHistory history) {
        numberingHistoryMapper.insert(history);
    }

    @Override
    public Optional<NumberingHistory> findHistoryById(Integer id) {
        return numberingHistoryMapper.findById(id);
    }

    @Override
    public Optional<NumberingHistory> findHistoryByCodeAndYearMonth(String numberingCode, String yearMonth) {
        return numberingHistoryMapper.findByCodeAndYearMonth(numberingCode, yearMonth);
    }

    @Override
    public List<NumberingHistory> findHistoriesByNumberingCode(String numberingCode) {
        return numberingHistoryMapper.findByNumberingCode(numberingCode);
    }

    @Override
    public List<NumberingHistory> findAllHistories() {
        return numberingHistoryMapper.findAll();
    }

    @Override
    public void incrementLastNumber(Integer historyId) {
        numberingHistoryMapper.incrementLastNumber(historyId);
    }

    @Override
    public void updateHistory(NumberingHistory history) {
        numberingHistoryMapper.update(history);
    }

    @Override
    public void deleteHistoryById(Integer id) {
        numberingHistoryMapper.deleteById(id);
    }

    @Override
    public void deleteHistoriesByNumberingCode(String numberingCode) {
        numberingHistoryMapper.deleteByNumberingCode(numberingCode);
    }

    @Override
    public void deleteAllHistories() {
        numberingHistoryMapper.deleteAll();
    }
}
