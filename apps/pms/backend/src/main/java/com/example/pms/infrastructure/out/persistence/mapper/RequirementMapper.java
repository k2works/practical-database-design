package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.plan.Requirement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface RequirementMapper {
    void insert(Requirement requirement);
    Requirement findById(Integer id);
    Requirement findByRequirementNumber(String requirementNumber);
    /**
     * 所要番号で検索（引当を含む）.
     */
    Requirement findByRequirementNumberWithAllocations(String requirementNumber);
    List<Requirement> findByOrderId(Integer orderId);
    List<Requirement> findAll();
    void updateAllocation(@Param("id") Integer id,
                          @Param("allocatedQuantity") BigDecimal allocatedQuantity,
                          @Param("shortageQuantity") BigDecimal shortageQuantity);
    void deleteAll();
}
