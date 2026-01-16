package com.example.pms.application.service;

import com.example.pms.application.port.in.InspectionUseCase;
import com.example.pms.application.port.out.InspectionRepository;
import com.example.pms.domain.model.purchase.Inspection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 受入検査サービス.
 */
@Service
@Transactional
public class InspectionService implements InspectionUseCase {

    private final InspectionRepository inspectionRepository;

    public InspectionService(InspectionRepository inspectionRepository) {
        this.inspectionRepository = inspectionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inspection> getAllInspections() {
        return inspectionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inspection> getInspection(String inspectionNumber) {
        return inspectionRepository.findByInspectionNumber(inspectionNumber);
    }
}
