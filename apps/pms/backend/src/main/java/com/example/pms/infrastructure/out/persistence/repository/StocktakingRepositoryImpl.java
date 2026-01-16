package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StocktakingRepository;
import com.example.pms.domain.model.inventory.Stocktaking;
import com.example.pms.domain.model.inventory.StocktakingStatus;
import com.example.pms.infrastructure.out.persistence.mapper.StocktakingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 棚卸リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class StocktakingRepositoryImpl implements StocktakingRepository {

    private final StocktakingMapper stocktakingMapper;

    @Override
    public void save(Stocktaking stocktaking) {
        if (stocktaking.getId() == null) {
            stocktakingMapper.insert(stocktaking);
        } else {
            stocktakingMapper.update(stocktaking);
        }
    }

    @Override
    public Optional<Stocktaking> findById(Integer id) {
        return Optional.ofNullable(stocktakingMapper.findById(id));
    }

    @Override
    public Optional<Stocktaking> findByStocktakingNumber(String stocktakingNumber) {
        return Optional.ofNullable(stocktakingMapper.findByStocktakingNumber(stocktakingNumber));
    }

    @Override
    public Optional<Stocktaking> findByStocktakingNumberWithDetails(String stocktakingNumber) {
        return Optional.ofNullable(stocktakingMapper.findByStocktakingNumberWithDetails(stocktakingNumber));
    }

    @Override
    public List<Stocktaking> findByLocationCode(String locationCode) {
        return stocktakingMapper.findByLocationCode(locationCode);
    }

    @Override
    public List<Stocktaking> findByStatus(StocktakingStatus status) {
        return stocktakingMapper.findByStatus(status);
    }

    @Override
    public List<Stocktaking> findAll() {
        return stocktakingMapper.findAll();
    }

    @Override
    public List<Stocktaking> findWithPagination(int offset, int limit, String keyword) {
        return stocktakingMapper.findWithPagination(offset, limit, keyword);
    }

    @Override
    public long count(String keyword) {
        return stocktakingMapper.count(keyword);
    }

    @Override
    public void deleteByStocktakingNumber(String stocktakingNumber) {
        stocktakingMapper.deleteByStocktakingNumber(stocktakingNumber);
    }

    @Override
    public void deleteAll() {
        stocktakingMapper.deleteAll();
    }
}
