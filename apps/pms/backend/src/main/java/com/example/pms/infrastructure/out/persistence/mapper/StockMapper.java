package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.inventory.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
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

    List<Stock> findWithPagination(@Param("offset") int offset,
                                   @Param("limit") int limit,
                                   @Param("keyword") String keyword);

    long count(@Param("keyword") String keyword);

    void deleteAll();

    /**
     * 在庫増加（楽観ロック対応）.
     *
     * @return 更新行数（0 ならバージョン競合）
     */
    int increaseStock(@Param("locationCode") String locationCode,
                      @Param("itemCode") String itemCode,
                      @Param("quantity") BigDecimal quantity,
                      @Param("expectedVersion") Integer expectedVersion,
                      @Param("updatedBy") String updatedBy);

    /**
     * 在庫減少（楽観ロック対応）.
     *
     * @return 更新行数（0 ならバージョン競合）
     */
    int decreaseStock(@Param("locationCode") String locationCode,
                      @Param("itemCode") String itemCode,
                      @Param("quantity") BigDecimal quantity,
                      @Param("expectedVersion") Integer expectedVersion,
                      @Param("updatedBy") String updatedBy);

    /**
     * 在庫調整（楽観ロック対応）.
     *
     * @return 更新行数（0 ならバージョン競合）
     */
    int updateWithOptimisticLock(@Param("stock") Stock stock,
                                 @Param("expectedVersion") Integer expectedVersion);
}
