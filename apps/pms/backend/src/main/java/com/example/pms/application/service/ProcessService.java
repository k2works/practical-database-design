package com.example.pms.application.service;

import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.application.port.in.command.CreateProcessCommand;
import com.example.pms.application.port.in.command.UpdateProcessCommand;
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

    @Override
    public Process createProcess(CreateProcessCommand command) {
        Process process = Process.builder()
            .processCode(command.processCode())
            .processName(command.processName())
            .processType(command.processType())
            .locationCode(command.locationCode())
            .build();
        processRepository.save(process);
        return process;
    }

    @Override
    public Process updateProcess(String processCode, UpdateProcessCommand command) {
        Process process = Process.builder()
            .processCode(processCode)
            .processName(command.processName())
            .processType(command.processType())
            .locationCode(command.locationCode())
            .build();
        processRepository.update(process);
        return process;
    }

    @Override
    public void deleteProcess(String processCode) {
        processRepository.deleteByProcessCode(processCode);
    }
}
