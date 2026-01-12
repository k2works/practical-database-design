package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ProcessInspectionResultRepository;
import com.example.pms.domain.model.quality.ProcessInspectionResult;
import com.example.pms.infrastructure.out.persistence.mapper.ProcessInspectionResultMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工程検査結果リポジトリ実装.
 */
@Repository
public class ProcessInspectionResultRepositoryImpl implements ProcessInspectionResultRepository {

    private final ProcessInspectionResultMapper processInspectionResultMapper;

    public ProcessInspectionResultRepositoryImpl(ProcessInspectionResultMapper processInspectionResultMapper) {
        this.processInspectionResultMapper = processInspectionResultMapper;
    }

    @Override
    public void save(ProcessInspectionResult result) {
        processInspectionResultMapper.insert(result);
    }

    @Override
    public Optional<ProcessInspectionResult> findById(Integer id) {
        return Optional.ofNullable(processInspectionResultMapper.findById(id));
    }

    @Override
    public List<ProcessInspectionResult> findByInspectionNumber(String inspectionNumber) {
        return processInspectionResultMapper.findByInspectionNumber(inspectionNumber);
    }

    @Override
    public void update(ProcessInspectionResult result) {
        processInspectionResultMapper.update(result);
    }

    @Override
    public void deleteByInspectionNumber(String inspectionNumber) {
        processInspectionResultMapper.deleteByInspectionNumber(inspectionNumber);
    }

    @Override
    public void deleteById(Integer id) {
        processInspectionResultMapper.deleteById(id);
    }
}
