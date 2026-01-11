package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.process.Process;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProcessMapper {
    void insert(Process process);
    Optional<Process> findByProcessCode(String processCode);
    List<Process> findAll();
    void update(Process process);
    void deleteAll();
}
