package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.process.Process;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProcessMapper {
    void insert(Process process);
    Optional<Process> findByProcessCode(String processCode);
    List<Process> findAll();
    void update(Process process);
    void deleteAll();

    List<Process> findWithPagination(
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long count(@Param("keyword") String keyword);
}
