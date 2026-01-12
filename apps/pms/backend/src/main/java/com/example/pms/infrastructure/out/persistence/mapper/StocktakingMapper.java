package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.inventory.Stocktaking;
import com.example.pms.domain.model.inventory.StocktakingStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 棚卸 Mapper.
 */
@Mapper
public interface StocktakingMapper {

    void insert(Stocktaking stocktaking);

    void update(Stocktaking stocktaking);

    Stocktaking findById(Integer id);

    Stocktaking findByStocktakingNumber(String stocktakingNumber);

    /**
     * 棚卸番号で検索（明細を含む）.
     */
    Stocktaking findByStocktakingNumberWithDetails(String stocktakingNumber);

    List<Stocktaking> findByLocationCode(String locationCode);

    List<Stocktaking> findByStatus(@Param("status") StocktakingStatus status);

    List<Stocktaking> findAll();

    void deleteAll();
}
