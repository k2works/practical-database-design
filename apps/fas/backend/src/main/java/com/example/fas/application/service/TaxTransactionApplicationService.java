package com.example.fas.application.service;

import com.example.fas.application.port.in.TaxTransactionUseCase;
import com.example.fas.application.port.in.dto.CreateTaxTransactionCommand;
import com.example.fas.application.port.in.dto.TaxTransactionResponse;
import com.example.fas.application.port.in.dto.UpdateTaxTransactionCommand;
import com.example.fas.application.port.out.TaxTransactionRepository;
import com.example.fas.domain.exception.TaxTransactionAlreadyExistsException;
import com.example.fas.domain.exception.TaxTransactionNotFoundException;
import com.example.fas.domain.model.common.PageResult;
import com.example.fas.domain.model.tax.TaxTransaction;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 課税取引アプリケーションサービス.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxTransactionApplicationService implements TaxTransactionUseCase {

    private final TaxTransactionRepository taxTransactionRepository;

    @Override
    public TaxTransactionResponse getTaxTransaction(String taxCode) {
        TaxTransaction taxTransaction = taxTransactionRepository.findByCode(taxCode)
                .orElseThrow(() -> new TaxTransactionNotFoundException(taxCode));
        return TaxTransactionResponse.from(taxTransaction);
    }

    @Override
    public List<TaxTransactionResponse> getAllTaxTransactions() {
        return taxTransactionRepository.findAll().stream()
                .map(TaxTransactionResponse::from)
                .toList();
    }

    @Override
    public PageResult<TaxTransactionResponse> getTaxTransactions(int page, int size, String keyword) {
        PageResult<TaxTransaction> pageResult = taxTransactionRepository.findWithPagination(page, size, keyword);
        List<TaxTransactionResponse> content = pageResult.getContent().stream()
                .map(TaxTransactionResponse::from)
                .toList();
        return new PageResult<>(content, pageResult.getPage(), pageResult.getSize(), pageResult.getTotalElements());
    }

    @Override
    @Transactional
    public TaxTransactionResponse createTaxTransaction(CreateTaxTransactionCommand command) {
        if (taxTransactionRepository.findByCode(command.getTaxCode()).isPresent()) {
            throw new TaxTransactionAlreadyExistsException(command.getTaxCode());
        }

        TaxTransaction taxTransaction = TaxTransaction.builder()
                .taxCode(command.getTaxCode())
                .taxName(command.getTaxName())
                .taxRate(command.getTaxRate())
                .build();

        taxTransactionRepository.save(taxTransaction);

        return TaxTransactionResponse.from(taxTransaction);
    }

    @Override
    @Transactional
    public TaxTransactionResponse updateTaxTransaction(String taxCode, UpdateTaxTransactionCommand command) {
        TaxTransaction taxTransaction = taxTransactionRepository.findByCode(taxCode)
                .orElseThrow(() -> new TaxTransactionNotFoundException(taxCode));

        if (command.getTaxName() != null) {
            taxTransaction.setTaxName(command.getTaxName());
        }
        if (command.getTaxRate() != null) {
            taxTransaction.setTaxRate(command.getTaxRate());
        }

        taxTransactionRepository.update(taxTransaction);

        return TaxTransactionResponse.from(taxTransaction);
    }

    @Override
    @Transactional
    public void deleteTaxTransaction(String taxCode) {
        taxTransactionRepository.findByCode(taxCode)
                .orElseThrow(() -> new TaxTransactionNotFoundException(taxCode));
        taxTransactionRepository.delete(taxCode);
    }
}
