package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.sales.Quotation;
import com.example.sms.domain.model.sales.QuotationDetail;
import com.example.sms.domain.model.sales.QuotationStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 見積マッパー.
 */
@Mapper
public interface QuotationMapper {

    void insertHeader(Quotation quotation);

    void insertDetail(QuotationDetail detail);

    Optional<Quotation> findById(Integer id);

    Optional<Quotation> findByQuotationNumber(String quotationNumber);

    List<Quotation> findByCustomerCode(String customerCode);

    List<Quotation> findByStatus(@Param("status") QuotationStatus status);

    List<Quotation> findByQuotationDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<Quotation> findAll();

    /**
     * ページネーション付きで見積を検索.
     */
    List<Quotation> findWithPagination(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("keyword") String keyword);

    /**
     * 検索条件に一致する見積の件数を取得.
     */
    long count(@Param("keyword") String keyword);

    List<QuotationDetail> findDetailsByQuotationId(Integer quotationId);

    void updateHeader(Quotation quotation);

    void deleteDetailsByQuotationId(Integer quotationId);

    void deleteById(Integer id);

    void deleteAllDetails();

    void deleteAll();
}
