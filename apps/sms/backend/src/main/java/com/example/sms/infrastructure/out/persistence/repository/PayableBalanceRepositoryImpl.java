package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.PayableBalanceRepository;
import com.example.sms.domain.model.payment.PayableBalance;
import com.example.sms.infrastructure.out.persistence.mapper.PayableBalanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 買掛金残高リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class PayableBalanceRepositoryImpl implements PayableBalanceRepository {

    private final PayableBalanceMapper payableBalanceMapper;

    @Override
    public void save(PayableBalance payableBalance) {
        payableBalanceMapper.insert(payableBalance);
    }

    @Override
    public Optional<PayableBalance> findById(Integer id) {
        return payableBalanceMapper.findById(id);
    }

    @Override
    public Optional<PayableBalance> findBySupplierAndYearMonth(String supplierCode, LocalDate yearMonth) {
        return payableBalanceMapper.findBySupplierAndYearMonth(supplierCode, yearMonth);
    }

    @Override
    public List<PayableBalance> findBySupplierCode(String supplierCode) {
        return payableBalanceMapper.findBySupplierCode(supplierCode);
    }

    @Override
    public List<PayableBalance> findByYearMonth(LocalDate yearMonth) {
        return payableBalanceMapper.findByYearMonth(yearMonth);
    }

    @Override
    public List<PayableBalance> findAll() {
        return payableBalanceMapper.findAll();
    }

    @Override
    public void update(PayableBalance payableBalance) {
        payableBalanceMapper.update(payableBalance);
    }

    @Override
    public void deleteById(Integer id) {
        payableBalanceMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        payableBalanceMapper.deleteAll();
    }
}
