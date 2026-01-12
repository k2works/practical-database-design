package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.inventory.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 在庫情報 Mapper.
 */
@Mapper
public interface StockMapper {

    void insert(Stock stock);

    void update(Stock stock);

    Stock findById(Integer id);

    Stock findByLocationAndItem(@Param("locationCode") String locationCode,
                                @Param("itemCode") String itemCode);

    List<Stock> findByLocation(String locationCode);

    List<Stock> findByItem(String itemCode);

    List<Stock> findAll();

    void deleteAll();
}
