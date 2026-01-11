package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.purchase.Inspection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InspectionMapper {
    void insert(Inspection inspection);
    Inspection findById(Integer id);
    Inspection findByInspectionNumber(String inspectionNumber);
    List<Inspection> findByReceivingNumber(String receivingNumber);
    List<Inspection> findAll();
    void deleteAll();
}
