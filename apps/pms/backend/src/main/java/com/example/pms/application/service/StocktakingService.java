package com.example.pms.application.service;

import com.example.pms.application.port.in.StocktakingUseCase;
import com.example.pms.application.port.out.StocktakingRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Stocktaking;
import com.example.pms.domain.model.inventory.StocktakingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 棚卸サービス（Application Service）.
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
    public PageResult<Stocktaking> getStocktakingList(int page, int size, String keyword) {
        int offset = page * size;
        List<Stocktaking> content = stocktakingRepository.findWithPagination(offset, size, keyword);
        long totalElements = stocktakingRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stocktaking> getAllStocktakings() {
        return stocktakingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Stocktaking> getStocktaking(String stocktakingNumber) {
        return stocktakingRepository.findByStocktakingNumber(stocktakingNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Stocktaking> getStocktakingWithDetails(String stocktakingNumber) {
        return stocktakingRepository.findByStocktakingNumberWithDetails(stocktakingNumber);
    }

    @Override
    public Stocktaking createStocktaking(Stocktaking stocktaking) {
        String stocktakingNumber = generateStocktakingNumber();
        stocktaking.setStocktakingNumber(stocktakingNumber);
        stocktaking.setCreatedBy("system");
        stocktaking.setUpdatedBy("system");

        setDefaultValues(stocktaking);
        normalizeOptionalFields(stocktaking);

        stocktakingRepository.save(stocktaking);
        return stocktakingRepository.findByStocktakingNumber(stocktakingNumber)
            .orElseThrow(() -> new IllegalStateException("棚卸の登録に失敗しました"));
    }

    private void setDefaultValues(Stocktaking stocktaking) {
        if (stocktaking.getStatus() == null) {
            stocktaking.setStatus(StocktakingStatus.ISSUED);
        }
        if (stocktaking.getVersion() == null) {
            stocktaking.setVersion(1);
        }
    }

    private void normalizeOptionalFields(Stocktaking stocktaking) {
        stocktaking.setLocationCode(emptyToNull(stocktaking.getLocationCode()));
    }

    private String emptyToNull(String value) {
        return (value != null && value.isEmpty()) ? null : value;
    }

    @Override
    public Stocktaking updateStocktaking(String stocktakingNumber, Stocktaking stocktaking) {
        Stocktaking existing = stocktakingRepository.findByStocktakingNumber(stocktakingNumber)
            .orElseThrow(() -> new IllegalStateException("棚卸が見つかりません: " + stocktakingNumber));

        existing.setLocationCode(emptyToNull(stocktaking.getLocationCode()));
        existing.setStocktakingDate(stocktaking.getStocktakingDate());
        existing.setStatus(stocktaking.getStatus());
        existing.setUpdatedBy("system");

        stocktakingRepository.save(existing);
        return stocktakingRepository.findByStocktakingNumber(stocktakingNumber)
            .orElseThrow(() -> new IllegalStateException("棚卸の更新に失敗しました"));
    }

    @Override
    public void deleteStocktaking(String stocktakingNumber) {
        stocktakingRepository.deleteByStocktakingNumber(stocktakingNumber);
    }

    private String generateStocktakingNumber() {
        String datePrefix = "ST-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        List<Stocktaking> allStocktakings = stocktakingRepository.findAll();

        int maxSeq = allStocktakings.stream()
            .map(Stocktaking::getStocktakingNumber)
            .filter(num -> num != null && num.startsWith(datePrefix))
            .map(num -> {
                String seqStr = num.substring(datePrefix.length());
                try {
                    return Integer.parseInt(seqStr);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max(Integer::compareTo)
            .orElse(0);

        return datePrefix + String.format("%04d", maxSeq + 1);
    }
}
