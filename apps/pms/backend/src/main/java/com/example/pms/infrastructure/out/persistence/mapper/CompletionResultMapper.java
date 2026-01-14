package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.process.CompletionResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 完成実績 Mapper.
 */
@Mapper
public interface CompletionResultMapper {

    void insert(CompletionResult completionResult);

    CompletionResult findById(Integer id);

    CompletionResult findByCompletionResultNumber(String completionResultNumber);

    List<CompletionResult> findByWorkOrderNumber(String workOrderNumber);

    List<CompletionResult> findAll();

    List<CompletionResult> findWithPagination(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);

    long count(@Param("keyword") String keyword);

    void update(CompletionResult completionResult);

    void deleteByCompletionResultNumber(String completionResultNumber);

    void deleteAll();
}
