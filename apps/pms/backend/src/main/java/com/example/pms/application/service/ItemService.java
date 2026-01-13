package com.example.pms.application.service;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.command.CreateItemCommand;
import com.example.pms.application.port.in.command.UpdateItemCommand;
import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.domain.exception.DuplicateItemException;
import com.example.pms.domain.exception.ItemNotFoundException;
import com.example.pms.domain.model.item.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 品目サービス（Application Service）.
 */
@Service
@Transactional
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
public class ItemService implements ItemUseCase {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItem(String itemCode) {
        return itemRepository.findByItemCode(itemCode)
            .orElseThrow(() -> new ItemNotFoundException(itemCode));
    }

    @Override
    public Item createItem(CreateItemCommand command) {
        if (itemRepository.findByItemCode(command.getItemCode()).isPresent()) {
            throw new DuplicateItemException(command.getItemCode());
        }

        Item item = Item.builder()
            .itemCode(command.getItemCode())
            .itemName(command.getItemName())
            .itemCategory(command.getItemCategory())
            .unitCode(command.getUnitCode())
            .effectiveFrom(command.getEffectiveFrom() != null ? command.getEffectiveFrom() : LocalDate.now())
            .effectiveTo(command.getEffectiveTo())
            .leadTime(command.getLeadTime())
            .safetyLeadTime(command.getSafetyLeadTime())
            .safetyStock(command.getSafetyStock())
            .yieldRate(command.getYieldRate())
            .minLotSize(command.getMinLotSize())
            .lotIncrement(command.getLotIncrement())
            .maxLotSize(command.getMaxLotSize())
            .shelfLife(command.getShelfLife())
            .build();

        itemRepository.save(item);
        return item;
    }

    @Override
    public Item updateItem(String itemCode, UpdateItemCommand command) {
        Item existing = itemRepository.findByItemCode(itemCode)
            .orElseThrow(() -> new ItemNotFoundException(itemCode));

        Item updated = Item.builder()
            .id(existing.getId())
            .itemCode(existing.getItemCode())
            .itemName(command.getItemName() != null ? command.getItemName() : existing.getItemName())
            .itemCategory(command.getItemCategory() != null ? command.getItemCategory() : existing.getItemCategory())
            .unitCode(command.getUnitCode() != null ? command.getUnitCode() : existing.getUnitCode())
            .effectiveFrom(command.getEffectiveFrom() != null ? command.getEffectiveFrom() : existing.getEffectiveFrom())
            .effectiveTo(command.getEffectiveTo() != null ? command.getEffectiveTo() : existing.getEffectiveTo())
            .leadTime(command.getLeadTime() != null ? command.getLeadTime() : existing.getLeadTime())
            .safetyLeadTime(command.getSafetyLeadTime() != null ? command.getSafetyLeadTime() : existing.getSafetyLeadTime())
            .safetyStock(command.getSafetyStock() != null ? command.getSafetyStock() : existing.getSafetyStock())
            .yieldRate(command.getYieldRate() != null ? command.getYieldRate() : existing.getYieldRate())
            .minLotSize(command.getMinLotSize() != null ? command.getMinLotSize() : existing.getMinLotSize())
            .lotIncrement(command.getLotIncrement() != null ? command.getLotIncrement() : existing.getLotIncrement())
            .maxLotSize(command.getMaxLotSize() != null ? command.getMaxLotSize() : existing.getMaxLotSize())
            .shelfLife(command.getShelfLife() != null ? command.getShelfLife() : existing.getShelfLife())
            .createdAt(existing.getCreatedAt())
            .build();

        itemRepository.update(updated);
        return updated;
    }

    @Override
    public void deleteItem(String itemCode) {
        itemRepository.findByItemCode(itemCode)
            .orElseThrow(() -> new ItemNotFoundException(itemCode));
        // Note: 既存の ItemRepository には deleteByCode がないため、
        // 削除機能は ItemRepository に追加するか、論理削除を検討する必要がある
        throw new UnsupportedOperationException("品目の削除は現在サポートされていません");
    }
}
