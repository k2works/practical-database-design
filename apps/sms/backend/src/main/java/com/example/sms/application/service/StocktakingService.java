package com.example.sms.application.service;

import com.example.sms.application.port.in.StocktakingUseCase;
import com.example.sms.application.port.out.StocktakingRepository;
import com.example.sms.domain.exception.StocktakingNotFoundException;
import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 棚卸アプリケーションサービス.
 */
@Service
@Transactional
public class StocktakingService implements StocktakingUseCase {

    private final StocktakingRepository stocktakingRepository;

    public StocktakingService(StocktakingRepository stocktakingRepository) {
        this.stocktakingRepository = stocktakingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stocktaking> getAllStocktakings() {
        return stocktakingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Stocktaking getStocktakingByNumber(String stocktakingNumber) {
        return stocktakingRepository.findByStocktakingNumber(stocktakingNumber)
            .orElseThrow(() -> new StocktakingNotFoundException(stocktakingNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Stocktaking getStocktakingWithDetails(String stocktakingNumber) {
        return stocktakingRepository.findWithDetailsByStocktakingNumber(stocktakingNumber)
            .orElseThrow(() -> new StocktakingNotFoundException(stocktakingNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stocktaking> getStocktakingsByStatus(StocktakingStatus status) {
        return stocktakingRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stocktaking> getStocktakingsByWarehouse(String warehouseCode) {
        return stocktakingRepository.findByWarehouseCode(warehouseCode);
    }
}
