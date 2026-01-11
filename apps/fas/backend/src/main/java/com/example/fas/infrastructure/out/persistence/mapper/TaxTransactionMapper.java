package com.example.fas.infrastructure.out.persistence.mapper;

import com.example.fas.domain.model.tax.TaxTransaction;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 課税取引マッパー.
 */
@Mapper
public interface TaxTransactionMapper {

    void insert(TaxTransaction taxTransaction);

    Optional<TaxTransaction> findByCode(@Param("taxCode") String taxCode);

    List<TaxTransaction> findAll();

    /**
     * ページネーション付きで課税取引を検索.
     */
    List<TaxTransaction> findWithPagination(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("keyword") String keyword);

    /**
     * 検索条件に合致する件数を取得.
     */
    long count(@Param("keyword") String keyword);

    void update(TaxTransaction taxTransaction);

    /**
     * 課税取引を削除.
     */
    void delete(@Param("taxCode") String taxCode);

    void deleteAll();
}
