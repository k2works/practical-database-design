package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.StocktakingDetail;

import java.util.List;
import java.util.Optional;

/**
 * 棚卸明細リポジトリ.
 */
public interface StocktakingDetailRepository {

    void save(StocktakingDetail detail);

    Optional<StocktakingDetail> findById(Integer id);

    Optional<StocktakingDetail> findByStocktakingNumberAndLineNumber(String stocktakingNumber, Integer lineNumber);

    List<StocktakingDetail> findByStocktakingNumber(String stocktakingNumber);

    List<StocktakingDetail> findAll();

    void deleteByStocktakingNumber(String stocktakingNumber);

    void deleteAll();
}
