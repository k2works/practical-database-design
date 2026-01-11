package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ConsumptionDetailRepository;
import com.example.pms.domain.model.subcontract.ConsumptionDetail;
import com.example.pms.infrastructure.out.persistence.mapper.ConsumptionDetailMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消費明細データリポジトリ実装
 */
@Repository
public class ConsumptionDetailRepositoryImpl implements ConsumptionDetailRepository {

    private final ConsumptionDetailMapper consumptionDetailMapper;

    public ConsumptionDetailRepositoryImpl(ConsumptionDetailMapper consumptionDetailMapper) {
        this.consumptionDetailMapper = consumptionDetailMapper;
    }

    @Override
    public void save(ConsumptionDetail consumptionDetail) {
        consumptionDetailMapper.insert(consumptionDetail);
    }

    @Override
    public Optional<ConsumptionDetail> findById(Integer id) {
        return Optional.ofNullable(consumptionDetailMapper.findById(id));
    }

    @Override
    public Optional<ConsumptionDetail> findByConsumptionNumberAndLineNumber(
            String consumptionNumber, Integer lineNumber) {
        return Optional.ofNullable(consumptionDetailMapper.findByConsumptionNumberAndLineNumber(
                consumptionNumber, lineNumber));
    }

    @Override
    public List<ConsumptionDetail> findByConsumptionNumber(String consumptionNumber) {
        return consumptionDetailMapper.findByConsumptionNumber(consumptionNumber);
    }

    @Override
    public List<ConsumptionDetail> findAll() {
        return consumptionDetailMapper.findAll();
    }

    @Override
    public void deleteAll() {
        consumptionDetailMapper.deleteAll();
    }
}
