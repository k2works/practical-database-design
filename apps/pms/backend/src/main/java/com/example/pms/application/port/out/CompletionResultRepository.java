package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.CompletionResult;

import java.util.List;
import java.util.Optional;

/**
 * 完成実績リポジトリ.
 */
public interface CompletionResultRepository {

    void save(CompletionResult completionResult);

    Optional<CompletionResult> findById(Integer id);

    Optional<CompletionResult> findByCompletionResultNumber(String completionResultNumber);

    List<CompletionResult> findByWorkOrderNumber(String workOrderNumber);

    List<CompletionResult> findAll();

    void deleteAll();
}
