package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ConsumptionRepository;
import com.example.pms.domain.model.subcontract.Consumption;
import com.example.pms.infrastructure.out.persistence.mapper.ConsumptionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消費データリポジトリ実装
 */
@Repository
public class ConsumptionRepositoryImpl implements ConsumptionRepository {

    private final ConsumptionMapper consumptionMapper;

    public ConsumptionRepositoryImpl(ConsumptionMapper consumptionMapper) {
        this.consumptionMapper = consumptionMapper;
    }

    @Override
    public void save(Consumption consumption) {
        consumptionMapper.insert(consumption);
    }

    @Override
    public Optional<Consumption> findById(Integer id) {
        return Optional.ofNullable(consumptionMapper.findById(id));
    }

    @Override
    public Optional<Consumption> findByConsumptionNumber(String consumptionNumber) {
        return Optional.ofNullable(consumptionMapper.findByConsumptionNumber(consumptionNumber));
    }

    @Override
    public Optional<Consumption> findByConsumptionNumberWithDetails(String consumptionNumber) {
        return Optional.ofNullable(consumptionMapper.findByConsumptionNumberWithDetails(consumptionNumber));
    }

    @Override
    public List<Consumption> findByReceivingNumber(String receivingNumber) {
        return consumptionMapper.findByReceivingNumber(receivingNumber);
    }

    @Override
    public List<Consumption> findBySupplierCode(String supplierCode) {
        return consumptionMapper.findBySupplierCode(supplierCode);
    }

    @Override
    public List<Consumption> findAll() {
        return consumptionMapper.findAll();
    }

    @Override
    public void deleteAll() {
        consumptionMapper.deleteAll();
    }
}
