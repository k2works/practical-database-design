package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ProcessInspectionRepository;
import com.example.pms.domain.model.quality.ProcessInspection;
import com.example.pms.infrastructure.out.persistence.mapper.ProcessInspectionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工程検査リポジトリ実装.
 */
@Repository
public class ProcessInspectionRepositoryImpl implements ProcessInspectionRepository {

    private final ProcessInspectionMapper processInspectionMapper;

    public ProcessInspectionRepositoryImpl(ProcessInspectionMapper processInspectionMapper) {
        this.processInspectionMapper = processInspectionMapper;
    }

    @Override
    public void save(ProcessInspection inspection) {
        processInspectionMapper.insert(inspection);
    }

    @Override
    public Optional<ProcessInspection> findById(Integer id) {
        return Optional.ofNullable(processInspectionMapper.findById(id));
    }

    @Override
    public Optional<ProcessInspection> findByInspectionNumber(String inspectionNumber) {
        return Optional.ofNullable(processInspectionMapper.findByInspectionNumber(inspectionNumber));
    }

    @Override
    public Optional<ProcessInspection> findByInspectionNumberWithResults(String inspectionNumber) {
        return Optional.ofNullable(processInspectionMapper.findByInspectionNumberWithResults(inspectionNumber));
    }

    @Override
    public List<ProcessInspection> findByWorkOrderNumber(String workOrderNumber) {
        return processInspectionMapper.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public List<ProcessInspection> findByProcessCode(String processCode) {
        return processInspectionMapper.findByProcessCode(processCode);
    }

    @Override
    public List<ProcessInspection> findAll() {
        return processInspectionMapper.findAll();
    }

    @Override
    public int update(ProcessInspection inspection) {
        return processInspectionMapper.update(inspection);
    }

    @Override
    public void deleteByInspectionNumber(String inspectionNumber) {
        processInspectionMapper.deleteByInspectionNumber(inspectionNumber);
    }

    @Override
    public void deleteAll() {
        processInspectionMapper.deleteAll();
    }
}
