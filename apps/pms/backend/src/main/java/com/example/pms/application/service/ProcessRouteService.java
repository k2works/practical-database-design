package com.example.pms.application.service;

import com.example.pms.application.port.in.ProcessRouteUseCase;
import com.example.pms.application.port.out.ProcessRouteRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.ProcessRoute;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 工程表サービス（Application Service）.
 */
@Service
@Transactional
public class ProcessRouteService implements ProcessRouteUseCase {

    private final ProcessRouteRepository processRouteRepository;

    public ProcessRouteService(ProcessRouteRepository processRouteRepository) {
        this.processRouteRepository = processRouteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ProcessRoute> getProcessRoutes(int page, int size, String itemCode) {
        int offset = page * size;
        List<ProcessRoute> routes = processRouteRepository.findWithPagination(itemCode, size, offset);
        long totalElements = processRouteRepository.count(itemCode);
        return new PageResult<>(routes, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessRoute> getProcessRoutesByItemCode(String itemCode) {
        return processRouteRepository.findByItemCode(itemCode);
    }

    @Override
    public ProcessRoute createProcessRoute(ProcessRoute processRoute) {
        processRouteRepository.save(processRoute);
        return processRoute;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProcessRoute> getProcessRoute(String itemCode, Integer sequence) {
        return processRouteRepository.findByItemCodeAndSequence(itemCode, sequence);
    }

    @Override
    public ProcessRoute updateProcessRoute(String itemCode, Integer sequence, ProcessRoute processRoute) {
        processRouteRepository.update(processRoute);
        return processRoute;
    }

    @Override
    public void deleteProcessRoute(String itemCode, Integer sequence) {
        processRouteRepository.deleteByItemCodeAndSequence(itemCode, sequence);
    }
}
