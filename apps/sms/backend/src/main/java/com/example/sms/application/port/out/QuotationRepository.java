package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.sales.Quotation;
import com.example.sms.domain.model.sales.QuotationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 見積リポジトリ（Output Port）.
 */
public interface QuotationRepository {

    void save(Quotation quotation);

    Optional<Quotation> findById(Integer id);

    Optional<Quotation> findByQuotationNumber(String quotationNumber);

    Optional<Quotation> findWithDetailsByQuotationNumber(String quotationNumber);

    List<Quotation> findByCustomerCode(String customerCode);

    List<Quotation> findByStatus(QuotationStatus status);

    List<Quotation> findByQuotationDateBetween(LocalDate from, LocalDate to);

    List<Quotation> findAll();

    PageResult<Quotation> findWithPagination(int page, int size, String keyword);

    void update(Quotation quotation);

    void deleteById(Integer id);

    void deleteAll();
}
