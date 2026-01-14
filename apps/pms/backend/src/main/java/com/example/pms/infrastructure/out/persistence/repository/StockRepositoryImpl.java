package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StockRepository;
import com.example.pms.domain.model.inventory.Stock;
import com.example.pms.infrastructure.out.persistence.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 在庫情報リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private static final String SYSTEM_USER = "system";
    private final StockMapper stockMapper;

    @Override
    public void save(Stock stock) {
        if (stock.getId() == null) {
            stockMapper.insert(stock);
        } else {
            stockMapper.update(stock);
        }
    }

    @Override
    public Optional<Stock> findById(Integer id) {
        return Optional.ofNullable(stockMapper.findById(id));
    }

    @Override
    public Optional<Stock> findByLocationAndItem(String locationCode, String itemCode) {
        return Optional.ofNullable(stockMapper.findByLocationAndItem(locationCode, itemCode));
    }

    @Override
    public List<Stock> findByLocation(String locationCode) {
        return stockMapper.findByLocation(locationCode);
    }

    @Override
    public List<Stock> findByItem(String itemCode) {
        return stockMapper.findByItem(itemCode);
    }

    @Override
    public List<Stock> findAll() {
        return stockMapper.findAll();
    }

    @Override
    public List<Stock> findWithPagination(int offset, int limit, String keyword) {
        return stockMapper.findWithPagination(offset, limit, keyword);
    }

    @Override
    public long count(String keyword) {
        return stockMapper.count(keyword);
    }

    @Override
    public void deleteAll() {
        stockMapper.deleteAll();
    }

    @Override
    public boolean increase(String locationCode, String itemCode,
                            BigDecimal quantity, Integer expectedVersion) {
        int updatedRows = stockMapper.increaseStock(
                locationCode, itemCode, quantity, expectedVersion, SYSTEM_USER);
        return updatedRows > 0;
    }

    @Override
    public boolean decrease(String locationCode, String itemCode,
                            BigDecimal quantity, Integer expectedVersion) {
        int updatedRows = stockMapper.decreaseStock(
                locationCode, itemCode, quantity, expectedVersion, SYSTEM_USER);
        return updatedRows > 0;
    }

    @Override
    public boolean adjust(Stock stock, Integer expectedVersion) {
        int updatedRows = stockMapper.updateWithOptimisticLock(stock, expectedVersion);
        return updatedRows > 0;
    }
}
