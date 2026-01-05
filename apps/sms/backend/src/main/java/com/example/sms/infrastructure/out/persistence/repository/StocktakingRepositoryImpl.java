package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.StocktakingRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingDetail;
import com.example.sms.domain.model.inventory.StocktakingStatus;
import com.example.sms.infrastructure.out.persistence.mapper.StocktakingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    @Transactional
    public void save(Stocktaking stocktaking) {
        stocktakingMapper.insertHeader(stocktaking);

        if (stocktaking.getDetails() != null && !stocktaking.getDetails().isEmpty()) {
            int lineNumber = 1;
            for (StocktakingDetail detail : stocktaking.getDetails()) {
                detail.setStocktakingId(stocktaking.getId());
                detail.setLineNumber(lineNumber++);
                stocktakingMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public PageResult<Stocktaking> findWithPagination(int page, int size, String keyword, StocktakingStatus status) {
        int offset = page * size;
        List<Stocktaking> content = stocktakingMapper.findWithPagination(offset, size, keyword, status);
        long totalElements = stocktakingMapper.count(keyword, status);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    public Optional<Stocktaking> findById(Integer id) {
        return stocktakingMapper.findById(id);
    }

    @Override
    public Optional<Stocktaking> findByStocktakingNumber(String stocktakingNumber) {
        return stocktakingMapper.findByStocktakingNumber(stocktakingNumber);
    }

    @Override
    public Optional<Stocktaking> findWithDetailsByStocktakingNumber(String stocktakingNumber) {
        Stocktaking stocktaking = stocktakingMapper.findWithDetailsByStocktakingNumber(stocktakingNumber);
        return Optional.ofNullable(stocktaking);
    }

    @Override
    public List<Stocktaking> findByWarehouseCode(String warehouseCode) {
        return stocktakingMapper.findByWarehouseCode(warehouseCode);
    }

    @Override
    public List<Stocktaking> findByStatus(StocktakingStatus status) {
        return stocktakingMapper.findByStatus(status);
    }

    @Override
    public List<Stocktaking> findByStocktakingDateBetween(LocalDate from, LocalDate to) {
        return stocktakingMapper.findByStocktakingDateBetween(from, to);
    }

    @Override
    public List<Stocktaking> findAll() {
        return stocktakingMapper.findAll();
    }

    @Override
    @Transactional
    public void update(Stocktaking stocktaking) {
        int updatedCount = stocktakingMapper.updateWithOptimisticLock(stocktaking);

        if (updatedCount == 0) {
            Integer currentVersion = stocktakingMapper.findVersionById(stocktaking.getId());

            if (currentVersion == null) {
                throw new OptimisticLockException("棚卸", stocktaking.getId());
            } else {
                throw new OptimisticLockException(
                        "棚卸",
                        stocktaking.getId(),
                        stocktaking.getVersion(),
                        currentVersion
                );
            }
        }

        if (stocktaking.getDetails() != null) {
            stocktakingMapper.deleteDetailsByStocktakingId(stocktaking.getId());
            int lineNumber = 1;
            for (StocktakingDetail detail : stocktaking.getDetails()) {
                detail.setStocktakingId(stocktaking.getId());
                detail.setLineNumber(lineNumber++);
                stocktakingMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        stocktakingMapper.deleteDetailsByStocktakingId(id);
        stocktakingMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        stocktakingMapper.deleteAllDetails();
        stocktakingMapper.deleteAll();
    }
}
