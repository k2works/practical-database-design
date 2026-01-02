package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.RedBlackHistoryRepository;
import com.example.sms.domain.model.common.RedBlackHistory;
import com.example.sms.infrastructure.out.persistence.mapper.RedBlackHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 赤黒処理履歴リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class RedBlackHistoryRepositoryImpl implements RedBlackHistoryRepository {

    private final RedBlackHistoryMapper redBlackHistoryMapper;

    @Override
    public void save(RedBlackHistory history) {
        redBlackHistoryMapper.insert(history);
    }

    @Override
    public Optional<RedBlackHistory> findById(Integer id) {
        return redBlackHistoryMapper.findById(id);
    }

    @Override
    public Optional<RedBlackHistory> findByProcessNumber(String processNumber) {
        return redBlackHistoryMapper.findByProcessNumber(processNumber);
    }

    @Override
    public List<RedBlackHistory> findByOriginalSlipNumber(String originalSlipNumber) {
        return redBlackHistoryMapper.findByOriginalSlipNumber(originalSlipNumber);
    }

    @Override
    public List<RedBlackHistory> findBySlipCategory(String slipCategory) {
        return redBlackHistoryMapper.findBySlipCategory(slipCategory);
    }

    @Override
    public List<RedBlackHistory> findByProcessDateTimeBetween(LocalDateTime from, LocalDateTime to) {
        return redBlackHistoryMapper.findByProcessDateTimeBetween(from, to);
    }

    @Override
    public List<RedBlackHistory> findAll() {
        return redBlackHistoryMapper.findAll();
    }

    @Override
    public String findLatestProcessNumber(String prefix) {
        return redBlackHistoryMapper.findLatestProcessNumber(prefix);
    }

    @Override
    public void deleteById(Integer id) {
        redBlackHistoryMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        redBlackHistoryMapper.deleteAll();
    }
}
