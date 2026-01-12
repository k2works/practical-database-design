package com.example.pms.application.port.out;

import com.example.pms.domain.model.process.LaborHours;

import java.util.List;
import java.util.Optional;

/**
 * 工数実績リポジトリ.
 */
public interface LaborHoursRepository {

    void save(LaborHours laborHours);

    Optional<LaborHours> findById(Integer id);

    Optional<LaborHours> findByLaborHoursNumber(String laborHoursNumber);

    List<LaborHours> findByWorkOrderNumber(String workOrderNumber);

    List<LaborHours> findByWorkOrderNumberAndSequence(String workOrderNumber, Integer sequence);

    List<LaborHours> findAll();

    void deleteAll();
}
