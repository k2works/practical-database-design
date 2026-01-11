package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.Process;

import java.util.List;
import java.util.Optional;

/**
 * 工程マスタリポジトリインターフェース.
 */
public interface ProcessRepository {
    void save(Process process);
    Optional<Process> findByProcessCode(String processCode);
    List<Process> findAll();
    void update(Process process);
    void deleteAll();
}
