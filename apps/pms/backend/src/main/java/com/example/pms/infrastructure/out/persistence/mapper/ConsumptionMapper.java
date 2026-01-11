package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.subcontract.Consumption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ConsumptionMapper {
    void insert(Consumption consumption);
    Consumption findById(Integer id);
    Consumption findByConsumptionNumber(String consumptionNumber);
    List<Consumption> findByReceivingNumber(String receivingNumber);
    List<Consumption> findBySupplierCode(String supplierCode);
    List<Consumption> findAll();
    void deleteAll();
}
