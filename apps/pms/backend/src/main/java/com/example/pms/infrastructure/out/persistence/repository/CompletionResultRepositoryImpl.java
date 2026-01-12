package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.CompletionResultRepository;
import com.example.pms.domain.model.process.CompletionResult;
import com.example.pms.infrastructure.out.persistence.mapper.CompletionResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 完成実績リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class CompletionResultRepositoryImpl implements CompletionResultRepository {

    private final CompletionResultMapper completionResultMapper;

    @Override
    public void save(CompletionResult completionResult) {
        completionResultMapper.insert(completionResult);
    }

    @Override
    public Optional<CompletionResult> findById(Integer id) {
        return Optional.ofNullable(completionResultMapper.findById(id));
    }

    @Override
    public Optional<CompletionResult> findByCompletionResultNumber(String completionResultNumber) {
        return Optional.ofNullable(
                completionResultMapper.findByCompletionResultNumber(completionResultNumber));
    }

    @Override
    public List<CompletionResult> findByWorkOrderNumber(String workOrderNumber) {
        return completionResultMapper.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public List<CompletionResult> findAll() {
        return completionResultMapper.findAll();
    }

    @Override
    public void deleteAll() {
        completionResultMapper.deleteAll();
    }
}
