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

    void update(TaxTransaction taxTransaction);

    void deleteAll();
}
