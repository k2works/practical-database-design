package com.example.pms.application.port.out;

import com.example.pms.domain.model.inventory.Stock;

import java.util.List;
import java.util.Optional;

/**
 * 在庫情報リポジトリ.
 */
public interface StockRepository {

    void save(Stock stock);

    Optional<Stock> findById(Integer id);

    Optional<Stock> findByLocationAndItem(String locationCode, String itemCode);

    List<Stock> findByLocation(String locationCode);

    List<Stock> findByItem(String itemCode);

    List<Stock> findAll();

    void deleteAll();
}
