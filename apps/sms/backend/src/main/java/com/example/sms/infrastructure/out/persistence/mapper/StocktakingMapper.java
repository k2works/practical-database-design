package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingDetail;
import com.example.sms.domain.model.inventory.StocktakingStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 棚卸マッパー.
 */
@Mapper
public interface StocktakingMapper {

    void insertHeader(Stocktaking stocktaking);

    void insertDetail(StocktakingDetail detail);

    Optional<Stocktaking> findById(Integer id);

    Optional<Stocktaking> findByStocktakingNumber(String stocktakingNumber);

    Stocktaking findWithDetailsByStocktakingNumber(String stocktakingNumber);

    List<Stocktaking> findByWarehouseCode(String warehouseCode);

    List<Stocktaking> findByStatus(@Param("status") StocktakingStatus status);

    List<Stocktaking> findByStocktakingDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<Stocktaking> findAll();

    List<StocktakingDetail> findDetailsByStocktakingId(Integer stocktakingId);

    Integer findVersionById(Integer id);

    void updateHeader(Stocktaking stocktaking);

    int updateWithOptimisticLock(Stocktaking stocktaking);

    void updateDetail(StocktakingDetail detail);

    void deleteDetailsByStocktakingId(Integer stocktakingId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
