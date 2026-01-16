package com.example.pms.application.service;

import com.example.pms.application.port.in.StockUseCase;
import com.example.pms.application.port.out.StockRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Stock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 在庫照会サービス（Application Service）.
 */
@Service
@Transactional(readOnly = true)
public class StockService implements StockUseCase {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public PageResult<Stock> getStockList(int page, int size, String keyword) {
        int offset = page * size;
        List<Stock> content = stockRepository.findWithPagination(offset, size, keyword);
        long totalElements = stockRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    public Optional<Stock> getStock(Integer id) {
        return stockRepository.findById(id);
    }

    @Override
    public Optional<Stock> getStockByLocationAndItem(String locationCode, String itemCode) {
        return stockRepository.findByLocationAndItem(locationCode, itemCode);
    }
}
