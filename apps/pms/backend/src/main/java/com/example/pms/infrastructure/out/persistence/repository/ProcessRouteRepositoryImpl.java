package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ProcessRouteRepository;
import com.example.pms.domain.model.process.ProcessRoute;
import com.example.pms.infrastructure.out.persistence.mapper.ProcessRouteMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProcessRouteRepositoryImpl implements ProcessRouteRepository {

    private final ProcessRouteMapper processRouteMapper;

    public ProcessRouteRepositoryImpl(ProcessRouteMapper processRouteMapper) {
        this.processRouteMapper = processRouteMapper;
    }

    @Override
    public void save(ProcessRoute processRoute) {
        processRouteMapper.insert(processRoute);
    }

    @Override
    public Optional<ProcessRoute> findByItemCodeAndSequence(String itemCode, Integer sequence) {
        return processRouteMapper.findByItemCodeAndSequence(itemCode, sequence);
    }

    @Override
    public List<ProcessRoute> findByItemCode(String itemCode) {
        return processRouteMapper.findByItemCode(itemCode);
    }

    @Override
    public List<ProcessRoute> findAll() {
        return processRouteMapper.findAll();
    }

    @Override
    public void update(ProcessRoute processRoute) {
        processRouteMapper.update(processRoute);
    }

    @Override
    public void deleteByItemCode(String itemCode) {
        processRouteMapper.deleteByItemCode(itemCode);
    }

    @Override
    public void deleteAll() {
        processRouteMapper.deleteAll();
    }

    @Override
    public List<ProcessRoute> findWithPagination(String itemCode, int limit, int offset) {
        return processRouteMapper.findWithPagination(itemCode, limit, offset);
    }

    @Override
    public long count(String itemCode) {
        return processRouteMapper.count(itemCode);
    }

    @Override
    public void deleteByItemCodeAndSequence(String itemCode, Integer sequence) {
        processRouteMapper.deleteByItemCodeAndSequence(itemCode, sequence);
    }
}
