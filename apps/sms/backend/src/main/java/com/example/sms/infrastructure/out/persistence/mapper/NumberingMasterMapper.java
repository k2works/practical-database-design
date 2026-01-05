package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.common.NumberingMaster;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 採番マスタマッパー.
 */
@Mapper
public interface NumberingMasterMapper {

    void insert(NumberingMaster master);

    Optional<NumberingMaster> findByNumberingCode(@Param("numberingCode") String numberingCode);

    List<NumberingMaster> findAll();

    void update(NumberingMaster master);

    void deleteByNumberingCode(@Param("numberingCode") String numberingCode);

    void deleteAll();
}
