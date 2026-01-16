package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.LaborHoursRepository;
import com.example.pms.domain.model.process.LaborHours;
import com.example.pms.infrastructure.out.persistence.mapper.LaborHoursMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工数実績リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class LaborHoursRepositoryImpl implements LaborHoursRepository {

    private final LaborHoursMapper laborHoursMapper;

    @Override
    public void save(LaborHours laborHours) {
        laborHoursMapper.insert(laborHours);
    }

    @Override
    public Optional<LaborHours> findById(Integer id) {
        return Optional.ofNullable(laborHoursMapper.findById(id));
    }

    @Override
    public Optional<LaborHours> findByLaborHoursNumber(String laborHoursNumber) {
        return Optional.ofNullable(laborHoursMapper.findByLaborHoursNumber(laborHoursNumber));
    }

    @Override
    public List<LaborHours> findByWorkOrderNumber(String workOrderNumber) {
        return laborHoursMapper.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public List<LaborHours> findByWorkOrderNumberAndSequence(
            String workOrderNumber, Integer sequence) {
        return laborHoursMapper.findByWorkOrderNumberAndSequence(workOrderNumber, sequence);
    }

    @Override
    public List<LaborHours> findAll() {
        return laborHoursMapper.findAll();
    }

    @Override
    public List<LaborHours> findWithPagination(int offset, int limit, String keyword) {
        return laborHoursMapper.findWithPagination(offset, limit, keyword);
    }

    @Override
    public long count(String keyword) {
        return laborHoursMapper.count(keyword);
    }

    @Override
    public void update(LaborHours laborHours) {
        laborHoursMapper.update(laborHours);
    }

    @Override
    public void deleteByLaborHoursNumber(String laborHoursNumber) {
        laborHoursMapper.deleteByLaborHoursNumber(laborHoursNumber);
    }

    @Override
    public void deleteAll() {
        laborHoursMapper.deleteAll();
    }
}
