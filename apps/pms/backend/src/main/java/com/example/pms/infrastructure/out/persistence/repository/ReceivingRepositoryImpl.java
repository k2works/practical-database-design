package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ReceivingRepository;
import com.example.pms.domain.model.purchase.Receiving;
import com.example.pms.infrastructure.out.persistence.mapper.ReceivingMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 入荷受入データリポジトリ実装
 */
@Repository
public class ReceivingRepositoryImpl implements ReceivingRepository {

    private final ReceivingMapper receivingMapper;

    public ReceivingRepositoryImpl(ReceivingMapper receivingMapper) {
        this.receivingMapper = receivingMapper;
    }

    @Override
    public void save(Receiving receiving) {
        receivingMapper.insert(receiving);
    }

    @Override
    public Optional<Receiving> findById(Integer id) {
        return Optional.ofNullable(receivingMapper.findById(id));
    }

    @Override
    public Optional<Receiving> findByReceivingNumber(String receivingNumber) {
        return Optional.ofNullable(receivingMapper.findByReceivingNumber(receivingNumber));
    }

    @Override
    public Optional<Receiving> findByReceivingNumberWithInspections(String receivingNumber) {
        return Optional.ofNullable(receivingMapper.findByReceivingNumberWithInspections(receivingNumber));
    }

    @Override
    public List<Receiving> findByPurchaseOrderNumber(String purchaseOrderNumber) {
        return receivingMapper.findByPurchaseOrderNumber(purchaseOrderNumber);
    }

    @Override
    public List<Receiving> findByPurchaseOrderNumberAndLineNumber(
            String purchaseOrderNumber, Integer lineNumber) {
        return receivingMapper.findByPurchaseOrderNumberAndLineNumber(purchaseOrderNumber, lineNumber);
    }

    @Override
    public List<Receiving> findAll() {
        return receivingMapper.findAll();
    }

    @Override
    public void deleteAll() {
        receivingMapper.deleteAll();
    }
}
