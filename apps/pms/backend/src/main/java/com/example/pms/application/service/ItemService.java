package com.example.pms.application.service;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.command.CreateItemCommand;
import com.example.pms.application.port.in.command.UpdateItemCommand;
import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.domain.exception.DuplicateItemException;
import com.example.pms.domain.exception.ItemNotFoundException;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
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
    public List<Item> getItemsByCategory(ItemCategory category) {
        return itemRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> searchItems(String keyword) {
        return itemRepository.searchByKeyword(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItem(String itemCode) {
        return itemRepository.findByItemCode(itemCode)
            .orElseThrow(() -> new ItemNotFoundException(itemCode));
    }

    @Override
    public Item createItem(CreateItemCommand command) {
        if (itemRepository.findByItemCode(command.itemCode()).isPresent()) {
            throw new DuplicateItemException(command.itemCode());
        }

        Item item = Item.builder()
            .itemCode(command.itemCode())
            .itemName(command.itemName())
            .itemCategory(command.itemCategory())
            .unitCode(command.unitCode())
            .effectiveFrom(command.effectiveFrom() != null ? command.effectiveFrom() : LocalDate.now())
            .effectiveTo(command.effectiveTo())
            .leadTime(command.leadTime())
            .safetyLeadTime(command.safetyLeadTime())
            .safetyStock(command.safetyStock())
            .yieldRate(command.yieldRate())
            .minLotSize(command.minLotSize())
            .lotIncrement(command.lotIncrement())
            .maxLotSize(command.maxLotSize())
            .shelfLife(command.shelfLife())
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
            .itemName(command.itemName() != null ? command.itemName() : existing.getItemName())
            .itemCategory(command.itemCategory() != null ? command.itemCategory() : existing.getItemCategory())
            .unitCode(command.unitCode() != null ? command.unitCode() : existing.getUnitCode())
            .effectiveFrom(command.effectiveFrom() != null ? command.effectiveFrom() : existing.getEffectiveFrom())
            .effectiveTo(command.effectiveTo() != null ? command.effectiveTo() : existing.getEffectiveTo())
            .leadTime(command.leadTime() != null ? command.leadTime() : existing.getLeadTime())
            .safetyLeadTime(command.safetyLeadTime() != null ? command.safetyLeadTime() : existing.getSafetyLeadTime())
            .safetyStock(command.safetyStock() != null ? command.safetyStock() : existing.getSafetyStock())
            .yieldRate(command.yieldRate() != null ? command.yieldRate() : existing.getYieldRate())
            .minLotSize(command.minLotSize() != null ? command.minLotSize() : existing.getMinLotSize())
            .lotIncrement(command.lotIncrement() != null ? command.lotIncrement() : existing.getLotIncrement())
            .maxLotSize(command.maxLotSize() != null ? command.maxLotSize() : existing.getMaxLotSize())
            .shelfLife(command.shelfLife() != null ? command.shelfLife() : existing.getShelfLife())
            .createdAt(existing.getCreatedAt())
            .build();

        itemRepository.update(updated);
        return updated;
    }

    @Override
    public void deleteItem(String itemCode) {
        itemRepository.findByItemCode(itemCode)
            .orElseThrow(() -> new ItemNotFoundException(itemCode));
        itemRepository.deleteByItemCode(itemCode);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Item> getItems(int page, int size, ItemCategory category, String keyword) {
        int offset = page * size;
        List<Item> items = itemRepository.findWithPagination(category, keyword, size, offset);
        long totalElements = itemRepository.count(category, keyword);
        return new PageResult<>(items, page, size, totalElements);
    }
}
