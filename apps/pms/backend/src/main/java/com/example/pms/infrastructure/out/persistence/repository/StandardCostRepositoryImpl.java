package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StandardCostRepository;
import com.example.pms.domain.model.cost.StandardCost;
import com.example.pms.infrastructure.out.persistence.mapper.StandardCostMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 標準原価マスタリポジトリ実装.
 */
@Repository
public class StandardCostRepositoryImpl implements StandardCostRepository {

    private final StandardCostMapper mapper;

    public StandardCostRepositoryImpl(StandardCostMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(StandardCost standardCost) {
        mapper.insert(standardCost);
    }

    @Override
    public int update(StandardCost standardCost) {
        return mapper.update(standardCost);
    }

    @Override
    public Optional<StandardCost> findById(Integer id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    @Override
    public List<StandardCost> findByItemCode(String itemCode) {
        return mapper.findByItemCode(itemCode);
    }

    @Override
    public Optional<StandardCost> findValidByItemCode(String itemCode, LocalDate targetDate) {
        return Optional.ofNullable(mapper.findValidByItemCode(itemCode, targetDate));
    }

    @Override
    public List<StandardCost> findAll() {
        return mapper.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        mapper.deleteById(id);
    }

    @Override
    public void deleteByItemCode(String itemCode) {
        mapper.deleteByItemCode(itemCode);
    }

    @Override
    public void deleteAll() {
        mapper.deleteAll();
    }
}
