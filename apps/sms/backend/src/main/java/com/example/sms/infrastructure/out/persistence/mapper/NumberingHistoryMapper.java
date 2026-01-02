package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.common.NumberingHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 採番履歴マッパー.
 */
@Mapper
public interface NumberingHistoryMapper {

    void insert(NumberingHistory history);

    Optional<NumberingHistory> findById(@Param("id") Integer id);

    Optional<NumberingHistory> findByCodeAndYearMonth(
            @Param("numberingCode") String numberingCode,
            @Param("yearMonth") String yearMonth);

    List<NumberingHistory> findByNumberingCode(@Param("numberingCode") String numberingCode);

    List<NumberingHistory> findAll();

    void incrementLastNumber(@Param("id") Integer id);

    void update(NumberingHistory history);

    void deleteById(@Param("id") Integer id);

    void deleteByNumberingCode(@Param("numberingCode") String numberingCode);

    void deleteAll();
}
