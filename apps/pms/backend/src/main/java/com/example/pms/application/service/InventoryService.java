package com.example.pms.application.service;

import com.example.pms.application.port.in.InventoryUseCase;
import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.application.port.out.StockRepository;
import com.example.pms.domain.model.inventory.Stock;
import com.example.pms.domain.model.item.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在庫サービス（Application Service）.
 */
@Service
@Transactional(readOnly = true)
@SuppressWarnings("PMD.UseConcurrentHashMap")
public class InventoryService implements InventoryUseCase {

    private final StockRepository stockRepository;
    private final ItemRepository itemRepository;

    public InventoryService(StockRepository stockRepository, ItemRepository itemRepository) {
        this.stockRepository = stockRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Stock> getInventory(InventoryQuery query) {
        if (query == null) {
            return stockRepository.findAll();
        }

        if (query.getItemCode() != null && query.getLocationCode() != null) {
            return stockRepository.findByLocationAndItem(query.getLocationCode(), query.getItemCode())
                .map(List::of)
                .orElse(List.of());
        }

        if (query.getItemCode() != null) {
            return stockRepository.findByItem(query.getItemCode());
        }

        if (query.getLocationCode() != null) {
            return stockRepository.findByLocation(query.getLocationCode());
        }

        return stockRepository.findAll();
    }

    @Override
    public List<InventorySummary> getInventorySummary() {
        List<Stock> stocks = stockRepository.findAll();
        List<Item> items = itemRepository.findAll();

        // 品目コードをキーにしたマップを作成
        Map<String, Item> itemMap = new HashMap<>();
        for (Item item : items) {
            itemMap.put(item.getItemCode(), item);
        }

        // 品目ごとの在庫合計を計算
        Map<String, BigDecimal> stockTotals = new HashMap<>();
        for (Stock stock : stocks) {
            stockTotals.merge(stock.getItemCode(), stock.getStockQuantity(), BigDecimal::add);
        }

        List<InventorySummary> summaries = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : stockTotals.entrySet()) {
            String itemCode = entry.getKey();
            BigDecimal totalQuantity = entry.getValue();
            Item item = itemMap.get(itemCode);

            if (item != null) {
                BigDecimal safetyStock = item.getSafetyStock() != null ? item.getSafetyStock() : BigDecimal.ZERO;
                InventorySummary.StockState state = calculateStockState(totalQuantity, safetyStock);

                summaries.add(InventorySummary.builder()
                    .itemCode(itemCode)
                    .itemName(item.getItemName())
                    .totalQuantity(totalQuantity)
                    .safetyStock(safetyStock)
                    .stockState(state)
                    .build());
            }
        }

        return summaries;
    }

    @Override
    public List<InventorySummary> getShortageItems() {
        return getInventorySummary().stream()
            .filter(s -> s.getStockState() == InventorySummary.StockState.SHORTAGE)
            .toList();
    }

    @Override
    public Stock getStock(String itemCode, String locationCode) {
        return stockRepository.findByLocationAndItem(locationCode, itemCode)
            .orElse(Stock.empty(locationCode, itemCode));
    }

    private InventorySummary.StockState calculateStockState(BigDecimal totalQuantity, BigDecimal safetyStock) {
        if (totalQuantity.compareTo(safetyStock) < 0) {
            return InventorySummary.StockState.SHORTAGE;
        } else if (totalQuantity.compareTo(safetyStock.multiply(BigDecimal.valueOf(3))) > 0) {
            return InventorySummary.StockState.EXCESS;
        }
        return InventorySummary.StockState.NORMAL;
    }
}
