package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.QuotationRepository;
import com.example.sms.domain.model.sales.Quotation;
import com.example.sms.domain.model.sales.QuotationDetail;
import com.example.sms.domain.model.sales.QuotationStatus;
import com.example.sms.infrastructure.out.persistence.mapper.QuotationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 見積リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class QuotationRepositoryImpl implements QuotationRepository {

    private final QuotationMapper quotationMapper;

    @Override
    public void save(Quotation quotation) {
        quotationMapper.insertHeader(quotation);
        if (quotation.getDetails() != null) {
            for (QuotationDetail detail : quotation.getDetails()) {
                detail.setQuotationId(quotation.getId());
                quotationMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public Optional<Quotation> findById(Integer id) {
        return quotationMapper.findById(id);
    }

    @Override
    public Optional<Quotation> findByQuotationNumber(String quotationNumber) {
        return quotationMapper.findByQuotationNumber(quotationNumber);
    }

    @Override
    public Optional<Quotation> findWithDetailsByQuotationNumber(String quotationNumber) {
        return quotationMapper.findByQuotationNumber(quotationNumber)
            .map(quotation -> {
                List<QuotationDetail> details = quotationMapper.findDetailsByQuotationId(quotation.getId());
                quotation.setDetails(details);
                return quotation;
            });
    }

    @Override
    public List<Quotation> findByCustomerCode(String customerCode) {
        return quotationMapper.findByCustomerCode(customerCode);
    }

    @Override
    public List<Quotation> findByStatus(QuotationStatus status) {
        return quotationMapper.findByStatus(status);
    }

    @Override
    public List<Quotation> findByQuotationDateBetween(LocalDate from, LocalDate to) {
        return quotationMapper.findByQuotationDateBetween(from, to);
    }

    @Override
    public List<Quotation> findAll() {
        return quotationMapper.findAll();
    }

    @Override
    public void update(Quotation quotation) {
        quotationMapper.updateHeader(quotation);
        quotationMapper.deleteDetailsByQuotationId(quotation.getId());
        if (quotation.getDetails() != null) {
            for (QuotationDetail detail : quotation.getDetails()) {
                detail.setQuotationId(quotation.getId());
                quotationMapper.insertDetail(detail);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        quotationMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        quotationMapper.deleteAll();
    }
}
