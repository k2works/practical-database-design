package com.example.fas.application.port.out;

import com.example.fas.domain.model.tax.TaxTransaction;
import java.util.List;
import java.util.Optional;

/**
 * 課税取引リポジトリ（Output Port）.
 */
public interface TaxTransactionRepository {

    void save(TaxTransaction taxTransaction);

    Optional<TaxTransaction> findByCode(String taxCode);

    List<TaxTransaction> findAll();

    void update(TaxTransaction taxTransaction);

    void deleteAll();
}
