package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.subcontract.ConsumptionDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsumptionDetailMapper {
    void insert(ConsumptionDetail consumptionDetail);
    ConsumptionDetail findById(Integer id);
    ConsumptionDetail findByConsumptionNumberAndLineNumber(
            @Param("consumptionNumber") String consumptionNumber,
            @Param("lineNumber") Integer lineNumber);
    List<ConsumptionDetail> findByConsumptionNumber(String consumptionNumber);
    List<ConsumptionDetail> findAll();
    void deleteAll();
}
