package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StockRepository;
import com.example.pms.domain.model.inventory.Stock;
import com.example.pms.infrastructure.out.persistence.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 在庫情報リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

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
    public void deleteAll() {
        stockMapper.deleteAll();
    }
}
