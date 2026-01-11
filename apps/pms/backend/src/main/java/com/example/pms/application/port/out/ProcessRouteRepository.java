package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.ProcessRoute;

import java.util.List;
import java.util.Optional;

/**
 * 工程表リポジトリインターフェース.
 */
public interface ProcessRouteRepository {
    void save(ProcessRoute processRoute);
    Optional<ProcessRoute> findByItemCodeAndSequence(String itemCode, Integer sequence);
    List<ProcessRoute> findByItemCode(String itemCode);
    List<ProcessRoute> findAll();
    void update(ProcessRoute processRoute);
    void deleteByItemCode(String itemCode);
    void deleteAll();
}
