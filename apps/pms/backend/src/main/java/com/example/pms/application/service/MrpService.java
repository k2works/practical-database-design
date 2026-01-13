package com.example.pms.application.service;

import com.example.pms.application.port.in.MrpUseCase;
import com.example.pms.application.port.out.BomRepository;
import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.application.port.out.StockRepository;
import com.example.pms.domain.model.inventory.Stock;
import com.example.pms.domain.model.item.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MRP サービス（Application Service）.
 */
@Service
@Transactional
@SuppressWarnings({"PMD.UseConcurrentHashMap", "PMD.PrematureDeclaration"})
public class MrpService implements MrpUseCase {

    private final ItemRepository itemRepository;
    private final BomRepository bomRepository;
    private final StockRepository stockRepository;

    public MrpService(ItemRepository itemRepository,
                      BomRepository bomRepository,
                      StockRepository stockRepository) {
        this.itemRepository = itemRepository;
        this.bomRepository = bomRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MrpResult execute(LocalDate startDate, LocalDate endDate) {
        // 品目マスタを取得
        List<Item> items = itemRepository.findAll();
        Map<String, Item> itemMap = new HashMap<>();
        for (Item item : items) {
            itemMap.put(item.getItemCode(), item);
        }

        // 在庫情報を取得
        List<Stock> stocks = stockRepository.findAll();
        Map<String, BigDecimal> stockMap = new HashMap<>();
        for (Stock stock : stocks) {
            stockMap.merge(stock.getItemCode(), stock.getStockQuantity(), BigDecimal::add);
        }

        // 計画オーダと在庫不足品目を生成
        List<PlannedOrder> plannedOrders = new ArrayList<>();
        List<ShortageItem> shortageItems = new ArrayList<>();

        // 各品目について在庫と安全在庫を比較
        for (Item item : items) {
            BigDecimal currentStock = stockMap.getOrDefault(item.getItemCode(), BigDecimal.ZERO);
            BigDecimal safetyStock = item.getSafetyStock() != null ? item.getSafetyStock() : BigDecimal.ZERO;

            if (currentStock.compareTo(safetyStock) < 0) {
                BigDecimal shortage = safetyStock.subtract(currentStock);

                // 在庫不足として記録
                shortageItems.add(ShortageItem.builder()
                    .itemCode(item.getItemCode())
                    .itemName(item.getItemName())
                    .shortageQuantity(shortage)
                    .recommendedOrderDate(calculateOrderDate(item, startDate))
                    .build());

                // 計画オーダを生成
                String orderType = determineOrderType(item);
                plannedOrders.add(PlannedOrder.builder()
                    .itemCode(item.getItemCode())
                    .itemName(item.getItemName())
                    .orderType(orderType)
                    .quantity(calculateOrderQuantity(item, shortage))
                    .dueDate(startDate.plusDays(item.getLeadTime() != null ? item.getLeadTime() : 0))
                    .build());
            }
        }

        return MrpResult.builder()
            .executionTime(LocalDateTime.now())
            .periodStart(startDate)
            .periodEnd(endDate)
            .plannedOrders(plannedOrders)
            .shortageItems(shortageItems)
            .build();
    }

    private LocalDate calculateOrderDate(Item item, LocalDate dueDate) {
        int leadTime = item.getLeadTime() != null ? item.getLeadTime() : 0;
        int safetyLeadTime = item.getSafetyLeadTime() != null ? item.getSafetyLeadTime() : 0;
        return dueDate.minusDays(leadTime + safetyLeadTime);
    }

    private String determineOrderType(Item item) {
        // 品目区分に基づいて発注タイプを決定
        if (item.getItemCategory() == null) {
            return "PURCHASE";
        }

        return switch (item.getItemCategory()) {
            case PRODUCT, SEMI_PRODUCT, INTERMEDIATE -> "MANUFACTURING";
            case PART, MATERIAL, RAW_MATERIAL, SUPPLY -> "PURCHASE";
        };
    }

    private BigDecimal calculateOrderQuantity(Item item, BigDecimal shortage) {
        // ロットサイズを考慮して発注数量を計算
        BigDecimal minLotSize = item.getMinLotSize() != null ? item.getMinLotSize() : BigDecimal.ONE;
        BigDecimal lotIncrement = item.getLotIncrement() != null ? item.getLotIncrement() : BigDecimal.ONE;

        if (shortage.compareTo(minLotSize) <= 0) {
            return minLotSize;
        }

        // ロット増分で切り上げ
        BigDecimal excess = shortage.subtract(minLotSize);
        BigDecimal increments = excess.divide(lotIncrement, 0, java.math.RoundingMode.CEILING);
        return minLotSize.add(lotIncrement.multiply(increments));
    }
}
