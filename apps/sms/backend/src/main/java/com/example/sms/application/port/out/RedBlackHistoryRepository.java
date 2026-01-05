package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.RedBlackHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 赤黒処理履歴リポジトリ（Output Port）.
 */
public interface RedBlackHistoryRepository {

    void save(RedBlackHistory history);

    Optional<RedBlackHistory> findById(Integer id);

    Optional<RedBlackHistory> findByProcessNumber(String processNumber);

    List<RedBlackHistory> findByOriginalSlipNumber(String originalSlipNumber);

    List<RedBlackHistory> findBySlipCategory(String slipCategory);

    List<RedBlackHistory> findByProcessDateTimeBetween(LocalDateTime from, LocalDateTime to);

    List<RedBlackHistory> findAll();

    String findLatestProcessNumber(String prefix);

    void deleteById(Integer id);

    void deleteAll();
}
