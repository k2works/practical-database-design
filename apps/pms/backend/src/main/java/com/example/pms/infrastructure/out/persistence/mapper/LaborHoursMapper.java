package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.process.LaborHours;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工数実績 Mapper.
 */
@Mapper
public interface LaborHoursMapper {

    void insert(LaborHours laborHours);

    LaborHours findById(Integer id);

    LaborHours findByLaborHoursNumber(String laborHoursNumber);

    List<LaborHours> findByWorkOrderNumber(String workOrderNumber);

    List<LaborHours> findByWorkOrderNumberAndSequence(
            @Param("workOrderNumber") String workOrderNumber,
            @Param("sequence") Integer sequence);

    List<LaborHours> findAll();

    List<LaborHours> findWithPagination(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);

    long count(@Param("keyword") String keyword);

    void update(LaborHours laborHours);

    void deleteByLaborHoursNumber(String laborHoursNumber);

    void deleteAll();
}
