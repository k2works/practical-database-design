package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.AcceptanceRepository;
import com.example.pms.domain.model.purchase.Acceptance;
import com.example.pms.infrastructure.out.persistence.mapper.AcceptanceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 検収データリポジトリ実装
 */
@Repository
public class AcceptanceRepositoryImpl implements AcceptanceRepository {

    private final AcceptanceMapper acceptanceMapper;

    public AcceptanceRepositoryImpl(AcceptanceMapper acceptanceMapper) {
        this.acceptanceMapper = acceptanceMapper;
    }

    @Override
    public void save(Acceptance acceptance) {
        acceptanceMapper.insert(acceptance);
    }

    @Override
    public Optional<Acceptance> findById(Integer id) {
        return Optional.ofNullable(acceptanceMapper.findById(id));
    }

    @Override
    public Optional<Acceptance> findByAcceptanceNumber(String acceptanceNumber) {
        return Optional.ofNullable(acceptanceMapper.findByAcceptanceNumber(acceptanceNumber));
    }

    @Override
    public List<Acceptance> findByInspectionNumber(String inspectionNumber) {
        return acceptanceMapper.findByInspectionNumber(inspectionNumber);
    }

    @Override
    public List<Acceptance> findByPurchaseOrderNumber(String purchaseOrderNumber) {
        return acceptanceMapper.findByPurchaseOrderNumber(purchaseOrderNumber);
    }

    @Override
    public List<Acceptance> findAll() {
        return acceptanceMapper.findAll();
    }

    @Override
    public void deleteAll() {
        acceptanceMapper.deleteAll();
    }
}
