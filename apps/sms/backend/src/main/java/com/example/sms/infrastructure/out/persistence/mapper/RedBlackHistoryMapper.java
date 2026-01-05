package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.common.RedBlackHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 赤黒処理履歴マッパー.
 */
@Mapper
public interface RedBlackHistoryMapper {

    void insert(RedBlackHistory history);

    Optional<RedBlackHistory> findById(@Param("id") Integer id);

    Optional<RedBlackHistory> findByProcessNumber(@Param("processNumber") String processNumber);

    List<RedBlackHistory> findByOriginalSlipNumber(@Param("originalSlipNumber") String originalSlipNumber);

    List<RedBlackHistory> findBySlipCategory(@Param("slipCategory") String slipCategory);

    List<RedBlackHistory> findByProcessDateTimeBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    List<RedBlackHistory> findAll();

    String findLatestProcessNumber(@Param("prefix") String prefix);

    void deleteById(@Param("id") Integer id);

    void deleteAll();
}
