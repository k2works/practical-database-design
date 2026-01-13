package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemMapper {
    void insert(Item item);
    Optional<Item> findByItemCode(String itemCode);
    Optional<Item> findByItemCodeAndDate(@Param("itemCode") String itemCode,
                                          @Param("baseDate") LocalDate baseDate);
    List<Item> findAll();
    List<Item> findByCategory(@Param("category") ItemCategory category);
    List<Item> searchByKeyword(@Param("keyword") String keyword);
    void update(Item item);
    void deleteByItemCode(String itemCode);
    void deleteAll();

    /**
     * ページネーション付き検索.
     *
     * @param category 品目区分（null可）
     * @param keyword 検索キーワード（null可）
     * @param limit 取得件数
     * @param offset オフセット
     * @return 品目リスト
     */
    List<Item> findWithPagination(
            @Param("category") ItemCategory category,
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset);

    /**
     * 条件に一致する件数を取得.
     *
     * @param category 品目区分（null可）
     * @param keyword 検索キーワード（null可）
     * @return 件数
     */
    long count(
            @Param("category") ItemCategory category,
            @Param("keyword") String keyword);
}
