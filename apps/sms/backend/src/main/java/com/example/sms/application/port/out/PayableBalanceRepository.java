package com.example.sms.application.port.out;

import com.example.sms.domain.model.payment.PayableBalance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 買掛金残高リポジトリ（Output Port）.
 */
public interface PayableBalanceRepository {

    void save(PayableBalance payableBalance);

    Optional<PayableBalance> findById(Integer id);

    Optional<PayableBalance> findBySupplierAndYearMonth(String supplierCode, LocalDate yearMonth);

    List<PayableBalance> findBySupplierCode(String supplierCode);

    List<PayableBalance> findByYearMonth(LocalDate yearMonth);

    List<PayableBalance> findAll();

    void update(PayableBalance payableBalance);

    void deleteById(Integer id);

    void deleteAll();
}
