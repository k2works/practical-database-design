package com.example.sms.application.service;

import com.example.sms.application.port.in.StocktakingUseCase;
import com.example.sms.application.port.in.command.CreateStocktakingCommand;
import com.example.sms.application.port.out.StocktakingRepository;
import com.example.sms.domain.exception.StocktakingNotFoundException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.inventory.Stocktaking;
import com.example.sms.domain.model.inventory.StocktakingDetail;
import com.example.sms.domain.model.inventory.StocktakingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 棚卸アプリケーションサービス.
 */
@Service
@Transactional
public class StocktakingService implements StocktakingUseCase {

    private static final DateTimeFormatter STOCKTAKING_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final StocktakingRepository stocktakingRepository;

    public StocktakingService(StocktakingRepository stocktakingRepository) {
        this.stocktakingRepository = stocktakingRepository;
    }

    @Override
    public Stocktaking createStocktaking(CreateStocktakingCommand command) {
        String stocktakingNumber = generateStocktakingNumber();

        List<StocktakingDetail> details = new ArrayList<>();
        int lineNumber = 1;
        for (CreateStocktakingCommand.CreateStocktakingDetailCommand detailCmd : command.details()) {
            BigDecimal bookQty = detailCmd.bookQuantity() != null ? detailCmd.bookQuantity() : BigDecimal.ZERO;
            BigDecimal actualQty = detailCmd.actualQuantity() != null ? detailCmd.actualQuantity() : BigDecimal.ZERO;

            StocktakingDetail detail = StocktakingDetail.builder()
                .lineNumber(lineNumber++)
                .productCode(detailCmd.productCode())
                .locationCode(detailCmd.locationCode())
                .lotNumber(detailCmd.lotNumber())
                .bookQuantity(bookQty)
                .actualQuantity(actualQty)
                .differenceQuantity(actualQty.subtract(bookQty))
                .differenceReason(detailCmd.differenceReason())
                .adjustedFlag(false)
                .build();
            details.add(detail);
        }

        Stocktaking stocktaking = Stocktaking.builder()
            .stocktakingNumber(stocktakingNumber)
            .warehouseCode(command.warehouseCode())
            .stocktakingDate(command.stocktakingDate() != null ? command.stocktakingDate() : LocalDate.now())
            .status(StocktakingStatus.DRAFT)
            .remarks(command.remarks())
            .details(details)
            .build();

        stocktakingRepository.save(stocktaking);
        return stocktaking;
    }

    private String generateStocktakingNumber() {
        String datePrefix = LocalDate.now().format(STOCKTAKING_NUMBER_FORMAT);
        List<Stocktaking> todayStocktakings = stocktakingRepository.findByStocktakingDateBetween(
            LocalDate.now(), LocalDate.now());
        int sequence = todayStocktakings.size() + 1;
        return String.format("STK-%s-%04d", datePrefix, sequence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stocktaking> getAllStocktakings() {
        return stocktakingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Stocktaking> getStocktakings(int page, int size, String keyword, StocktakingStatus status) {
        return stocktakingRepository.findWithPagination(page, size, keyword, status);
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
