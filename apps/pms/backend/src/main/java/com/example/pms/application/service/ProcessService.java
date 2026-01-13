package com.example.pms.application.service;

import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.application.port.out.ProcessRepository;
import com.example.pms.domain.exception.ProcessNotFoundException;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.Process;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 工程サービス（Application Service）.
 */
@Service
@Transactional
public class ProcessService implements ProcessUseCase {

    private final ProcessRepository processRepository;

    public ProcessService(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Process> getProcesses(int page, int size, String keyword) {
        int offset = page * size;
        List<Process> processes = processRepository.findWithPagination(keyword, size, offset);
        long totalElements = processRepository.count(keyword);
        return new PageResult<>(processes, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public Process getProcess(String processCode) {
        return processRepository.findByProcessCode(processCode)
            .orElseThrow(() -> new ProcessNotFoundException(processCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Process> getAllProcesses() {
        return processRepository.findAll();
    }
}
