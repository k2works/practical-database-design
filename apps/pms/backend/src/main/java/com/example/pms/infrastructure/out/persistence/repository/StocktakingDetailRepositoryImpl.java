package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StocktakingDetailRepository;
import com.example.pms.domain.model.inventory.StocktakingDetail;
import com.example.pms.infrastructure.out.persistence.mapper.StocktakingDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 棚卸明細リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class StocktakingDetailRepositoryImpl implements StocktakingDetailRepository {

    private final StocktakingDetailMapper stocktakingDetailMapper;

    @Override
    public void save(StocktakingDetail detail) {
        if (detail.getId() == null) {
            stocktakingDetailMapper.insert(detail);
        } else {
            stocktakingDetailMapper.update(detail);
        }
    }

    @Override
    public Optional<StocktakingDetail> findById(Integer id) {
        return Optional.ofNullable(stocktakingDetailMapper.findById(id));
    }

    @Override
    public Optional<StocktakingDetail> findByStocktakingNumberAndLineNumber(
            String stocktakingNumber, Integer lineNumber) {
        return Optional.ofNullable(
                stocktakingDetailMapper.findByStocktakingNumberAndLineNumber(stocktakingNumber, lineNumber));
    }

    @Override
    public List<StocktakingDetail> findByStocktakingNumber(String stocktakingNumber) {
        return stocktakingDetailMapper.findByStocktakingNumber(stocktakingNumber);
    }

    @Override
    public List<StocktakingDetail> findAll() {
        return stocktakingDetailMapper.findAll();
    }

    @Override
    public void deleteByStocktakingNumber(String stocktakingNumber) {
        stocktakingDetailMapper.deleteByStocktakingNumber(stocktakingNumber);
    }

    @Override
    public void deleteAll() {
        stocktakingDetailMapper.deleteAll();
    }
}
