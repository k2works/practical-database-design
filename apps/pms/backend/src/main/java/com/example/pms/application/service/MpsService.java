package com.example.pms.application.service;

import com.example.pms.application.port.in.MpsUseCase;
import com.example.pms.application.port.out.MpsRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.PlanStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 基準生産計画サービス.
 */
@Service
@Transactional
public class MpsService implements MpsUseCase {

    private static final DateTimeFormatter MPS_NUMBER_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final MpsRepository mpsRepository;

    public MpsService(MpsRepository mpsRepository) {
        this.mpsRepository = mpsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<MasterProductionSchedule> getMpsList(int page, int size, PlanStatus status, String keyword) {
        int offset = page * size;
        List<MasterProductionSchedule> mpsList = mpsRepository.findWithPagination(status, keyword, size, offset);
        long totalElements = mpsRepository.count(status, keyword);
        return new PageResult<>(mpsList, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterProductionSchedule> getAllMps() {
        return mpsRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MasterProductionSchedule> getMps(String mpsNumber) {
        return mpsRepository.findByMpsNumber(mpsNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MasterProductionSchedule> getMpsWithOrders(String mpsNumber) {
        return mpsRepository.findByMpsNumberWithOrders(mpsNumber);
    }

    @Override
    public MasterProductionSchedule createMps(MasterProductionSchedule mps) {
        String mpsNumber = generateMpsNumber(mps.getPlanDate());
        MasterProductionSchedule newMps = MasterProductionSchedule.builder()
            .mpsNumber(mpsNumber)
            .planDate(mps.getPlanDate())
            .itemCode(mps.getItemCode())
            .planQuantity(mps.getPlanQuantity())
            .dueDate(mps.getDueDate())
            .status(PlanStatus.DRAFT)
            .locationCode(mps.getLocationCode())
            .remarks(mps.getRemarks())
            .createdBy(mps.getCreatedBy())
            .updatedBy(mps.getUpdatedBy())
            .build();
        mpsRepository.save(newMps);
        return newMps;
    }

    @Override
    public MasterProductionSchedule updateMps(String mpsNumber, MasterProductionSchedule mps) {
        MasterProductionSchedule existing = mpsRepository.findByMpsNumber(mpsNumber)
            .orElseThrow(() -> new IllegalArgumentException("MPS not found: " + mpsNumber));

        MasterProductionSchedule updated = MasterProductionSchedule.builder()
            .id(existing.getId())
            .mpsNumber(existing.getMpsNumber())
            .planDate(mps.getPlanDate())
            .itemCode(mps.getItemCode())
            .planQuantity(mps.getPlanQuantity())
            .dueDate(mps.getDueDate())
            .status(existing.getStatus())
            .locationCode(mps.getLocationCode())
            .remarks(mps.getRemarks())
            .updatedBy(mps.getUpdatedBy())
            .version(existing.getVersion())
            .build();

        mpsRepository.update(updated);
        return updated;
    }

    @Override
    public void confirmMps(String mpsNumber) {
        MasterProductionSchedule mps = mpsRepository.findByMpsNumber(mpsNumber)
            .orElseThrow(() -> new IllegalArgumentException("MPS not found: " + mpsNumber));

        if (mps.getStatus() != PlanStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT MPS can be confirmed");
        }

        mpsRepository.updateStatus(mps.getId(), PlanStatus.CONFIRMED);
    }

    @Override
    public void cancelMps(String mpsNumber) {
        MasterProductionSchedule mps = mpsRepository.findByMpsNumber(mpsNumber)
            .orElseThrow(() -> new IllegalArgumentException("MPS not found: " + mpsNumber));

        if (mps.getStatus() == PlanStatus.CANCELLED) {
            throw new IllegalStateException("MPS is already cancelled");
        }

        mpsRepository.updateStatus(mps.getId(), PlanStatus.CANCELLED);
    }

    private String generateMpsNumber(LocalDate planDate) {
        String datePrefix = planDate.format(MPS_NUMBER_DATE_FORMAT);
        long count = mpsRepository.count(null, datePrefix);
        return String.format("MPS-%s-%04d", datePrefix, count + 1);
    }
}
