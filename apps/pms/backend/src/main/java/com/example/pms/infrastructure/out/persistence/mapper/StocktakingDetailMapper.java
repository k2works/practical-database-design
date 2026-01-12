package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.inventory.StocktakingDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 棚卸明細 Mapper.
 */
@Mapper
public interface StocktakingDetailMapper {

    void insert(StocktakingDetail detail);

    void update(StocktakingDetail detail);

    StocktakingDetail findById(Integer id);

    StocktakingDetail findByStocktakingNumberAndLineNumber(
            @Param("stocktakingNumber") String stocktakingNumber,
            @Param("lineNumber") Integer lineNumber);

    List<StocktakingDetail> findByStocktakingNumber(String stocktakingNumber);

    List<StocktakingDetail> findAll();

    void deleteAll();
}
