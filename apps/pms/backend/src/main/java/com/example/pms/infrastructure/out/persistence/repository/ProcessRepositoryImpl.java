package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.ProcessRepository;
import com.example.pms.domain.model.process.Process;
import com.example.pms.infrastructure.out.persistence.mapper.ProcessMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProcessRepositoryImpl implements ProcessRepository {

    private final ProcessMapper processMapper;

    public ProcessRepositoryImpl(ProcessMapper processMapper) {
        this.processMapper = processMapper;
    }

    @Override
    public void save(Process process) {
        processMapper.insert(process);
    }

    @Override
    public Optional<Process> findByProcessCode(String processCode) {
        return processMapper.findByProcessCode(processCode);
    }

    @Override
    public List<Process> findAll() {
        return processMapper.findAll();
    }

    @Override
    public void update(Process process) {
        processMapper.update(process);
    }

    @Override
    public void deleteAll() {
        processMapper.deleteAll();
    }
}
