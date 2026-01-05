package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.payment.PayableBalance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 買掛金残高 MyBatis Mapper.
 */
@Mapper
public interface PayableBalanceMapper {

    void insert(PayableBalance payableBalance);

    Optional<PayableBalance> findById(@Param("id") Integer id);

    Optional<PayableBalance> findBySupplierAndYearMonth(
            @Param("supplierCode") String supplierCode,
            @Param("yearMonth") LocalDate yearMonth);

    List<PayableBalance> findBySupplierCode(@Param("supplierCode") String supplierCode);

    List<PayableBalance> findByYearMonth(@Param("yearMonth") LocalDate yearMonth);

    List<PayableBalance> findAll();

    void update(PayableBalance payableBalance);

    void deleteById(@Param("id") Integer id);

    void deleteAll();
}
