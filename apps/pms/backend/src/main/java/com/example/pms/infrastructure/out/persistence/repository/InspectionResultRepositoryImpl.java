package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.InspectionResultRepository;
import com.example.pms.domain.model.process.InspectionResult;
import com.example.pms.infrastructure.out.persistence.mapper.InspectionResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 完成検査結果リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class InspectionResultRepositoryImpl implements InspectionResultRepository {

    private final InspectionResultMapper inspectionResultMapper;

    @Override
    public void save(InspectionResult inspectionResult) {
        inspectionResultMapper.insert(inspectionResult);
    }

    @Override
    public Optional<InspectionResult> findById(Integer id) {
        return Optional.ofNullable(inspectionResultMapper.findById(id));
    }

    @Override
    public Optional<InspectionResult> findByCompletionResultNumberAndDefectCode(
            String completionResultNumber, String defectCode) {
        return Optional.ofNullable(
                inspectionResultMapper.findByCompletionResultNumberAndDefectCode(
                        completionResultNumber, defectCode));
    }

    @Override
    public List<InspectionResult> findByCompletionResultNumber(String completionResultNumber) {
        return inspectionResultMapper.findByCompletionResultNumber(completionResultNumber);
    }

    @Override
    public List<InspectionResult> findAll() {
        return inspectionResultMapper.findAll();
    }

    @Override
    public void deleteAll() {
        inspectionResultMapper.deleteAll();
    }
}
