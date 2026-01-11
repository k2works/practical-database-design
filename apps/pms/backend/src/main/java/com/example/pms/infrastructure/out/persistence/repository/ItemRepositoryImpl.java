package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ItemRepository;
import com.example.pms.domain.model.item.Item;
import com.example.pms.infrastructure.out.persistence.mapper.ItemMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 品目リポジトリ実装
 */
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemMapper itemMapper;

    public ItemRepositoryImpl(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public void save(Item item) {
        itemMapper.insert(item);
    }

    @Override
    public Optional<Item> findByItemCode(String itemCode) {
        return itemMapper.findByItemCode(itemCode);
    }

    @Override
    public Optional<Item> findByItemCodeAndDate(String itemCode, LocalDate baseDate) {
        return itemMapper.findByItemCodeAndDate(itemCode, baseDate);
    }

    @Override
    public List<Item> findAll() {
        return itemMapper.findAll();
    }

    @Override
    public void update(Item item) {
        itemMapper.update(item);
    }

    @Override
    public void deleteAll() {
        itemMapper.deleteAll();
    }
}
