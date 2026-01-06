package com.example.fas.infrastructure.out.persistence.repository;

import com.example.fas.application.port.out.TaxTransactionRepository;
import com.example.fas.domain.model.tax.TaxTransaction;
import com.example.fas.infrastructure.out.persistence.mapper.TaxTransactionMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 課税取引リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class TaxTransactionRepositoryImpl implements TaxTransactionRepository {

    private final TaxTransactionMapper taxTransactionMapper;

    @Override
    public void save(TaxTransaction taxTransaction) {
        taxTransactionMapper.insert(taxTransaction);
    }

    @Override
    public Optional<TaxTransaction> findByCode(String taxCode) {
        return taxTransactionMapper.findByCode(taxCode);
    }

    @Override
    public List<TaxTransaction> findAll() {
        return taxTransactionMapper.findAll();
    }

    @Override
    public void update(TaxTransaction taxTransaction) {
        taxTransactionMapper.update(taxTransaction);
    }

    @Override
    public void deleteAll() {
        taxTransactionMapper.deleteAll();
    }
}
