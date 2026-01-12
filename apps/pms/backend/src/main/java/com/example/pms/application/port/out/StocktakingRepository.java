package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.Stocktaking;
import com.example.pms.domain.model.inventory.StocktakingStatus;

import java.util.List;
import java.util.Optional;

/**
 * 棚卸リポジトリ.
 */
public interface StocktakingRepository {

    void save(Stocktaking stocktaking);

    Optional<Stocktaking> findById(Integer id);

    Optional<Stocktaking> findByStocktakingNumber(String stocktakingNumber);

    List<Stocktaking> findByLocationCode(String locationCode);

    List<Stocktaking> findByStatus(StocktakingStatus status);

    List<Stocktaking> findAll();

    void deleteAll();
}
