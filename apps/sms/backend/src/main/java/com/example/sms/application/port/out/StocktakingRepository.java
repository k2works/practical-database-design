package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 棚卸リポジトリ（Output Port）.
 */
public interface StocktakingRepository {

    void save(Stocktaking stocktaking);

    PageResult<Stocktaking> findWithPagination(int page, int size, String keyword, StocktakingStatus status);

    Optional<Stocktaking> findById(Integer id);

    Optional<Stocktaking> findByStocktakingNumber(String stocktakingNumber);

    Optional<Stocktaking> findWithDetailsByStocktakingNumber(String stocktakingNumber);

    List<Stocktaking> findByWarehouseCode(String warehouseCode);

    List<Stocktaking> findByStatus(StocktakingStatus status);

    List<Stocktaking> findByStocktakingDateBetween(LocalDate from, LocalDate to);

    List<Stocktaking> findAll();

    void update(Stocktaking stocktaking);

    void deleteById(Integer id);

    void deleteAll();
}
