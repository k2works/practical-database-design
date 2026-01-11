package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.subcontract.SupplyDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SupplyDetailMapper {
    void insert(SupplyDetail supplyDetail);
    SupplyDetail findById(Integer id);
    SupplyDetail findBySupplyNumberAndLineNumber(
            @Param("supplyNumber") String supplyNumber,
            @Param("lineNumber") Integer lineNumber);
    List<SupplyDetail> findBySupplyNumber(String supplyNumber);
    List<SupplyDetail> findAll();
    void deleteAll();
}
