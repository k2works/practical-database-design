package com.example.pms.application.service;

import com.example.pms.application.port.in.LaborHoursUseCase;
import com.example.pms.application.port.out.LaborHoursRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.LaborHours;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 工数実績サービス（Application Service）.
 */
@Service
@Transactional
public class LaborHoursService implements LaborHoursUseCase {

    private final LaborHoursRepository laborHoursRepository;

    public LaborHoursService(LaborHoursRepository laborHoursRepository) {
        this.laborHoursRepository = laborHoursRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<LaborHours> getLaborHoursList(int page, int size, String keyword) {
        int offset = page * size;
        List<LaborHours> content = laborHoursRepository.findWithPagination(offset, size, keyword);
        long totalElements = laborHoursRepository.count(keyword);
        return new PageResult<>(content, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LaborHours> getAllLaborHours() {
        return laborHoursRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LaborHours> getLaborHours(String laborHoursNumber) {
        return laborHoursRepository.findByLaborHoursNumber(laborHoursNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LaborHours> getLaborHoursByWorkOrder(String workOrderNumber) {
        return laborHoursRepository.findByWorkOrderNumber(workOrderNumber);
    }

    @Override
    public LaborHours createLaborHours(LaborHours laborHours) {
        String laborHoursNumber = generateLaborHoursNumber();
        laborHours.setLaborHoursNumber(laborHoursNumber);
        laborHours.setCreatedBy("system");
        laborHours.setUpdatedBy("system");

        setDefaultValues(laborHours);
        normalizeOptionalFields(laborHours);

        laborHoursRepository.save(laborHours);
        return laborHoursRepository.findByLaborHoursNumber(laborHoursNumber)
            .orElseThrow(() -> new IllegalStateException("工数実績の登録に失敗しました"));
    }

    private void setDefaultValues(LaborHours laborHours) {
        if (laborHours.getHours() == null) {
            laborHours.setHours(BigDecimal.ZERO);
        }
        if (laborHours.getSequence() == null) {
            laborHours.setSequence(1);
        }
        if (laborHours.getVersion() == null) {
            laborHours.setVersion(1);
        }
    }

    private void normalizeOptionalFields(LaborHours laborHours) {
        laborHours.setProcessCode(emptyToNull(laborHours.getProcessCode()));
        laborHours.setDepartmentCode(emptyToNull(laborHours.getDepartmentCode()));
        laborHours.setEmployeeCode(emptyToNull(laborHours.getEmployeeCode()));
    }

    private String emptyToNull(String value) {
        return (value != null && value.isEmpty()) ? null : value;
    }

    @Override
    public LaborHours updateLaborHours(String laborHoursNumber, LaborHours laborHours) {
        LaborHours existing = laborHoursRepository.findByLaborHoursNumber(laborHoursNumber)
            .orElseThrow(() -> new IllegalStateException("工数実績が見つかりません: " + laborHoursNumber));

        existing.setWorkOrderNumber(laborHours.getWorkOrderNumber());
        existing.setItemCode(laborHours.getItemCode());
        existing.setSequence(laborHours.getSequence());
        existing.setProcessCode(emptyToNull(laborHours.getProcessCode()));
        existing.setDepartmentCode(emptyToNull(laborHours.getDepartmentCode()));
        existing.setEmployeeCode(emptyToNull(laborHours.getEmployeeCode()));
        existing.setWorkDate(laborHours.getWorkDate());
        existing.setHours(laborHours.getHours());
        existing.setRemarks(laborHours.getRemarks());
        existing.setUpdatedBy("system");

        laborHoursRepository.update(existing);
        return laborHoursRepository.findByLaborHoursNumber(laborHoursNumber)
            .orElseThrow(() -> new IllegalStateException("工数実績の更新に失敗しました"));
    }

    @Override
    public void deleteLaborHours(String laborHoursNumber) {
        laborHoursRepository.deleteByLaborHoursNumber(laborHoursNumber);
    }

    private String generateLaborHoursNumber() {
        String datePrefix = "LH-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        List<LaborHours> allLaborHours = laborHoursRepository.findAll();

        int maxSeq = allLaborHours.stream()
            .map(LaborHours::getLaborHoursNumber)
            .filter(num -> num != null && num.startsWith(datePrefix))
            .map(num -> {
                String seqStr = num.substring(datePrefix.length());
                try {
                    return Integer.parseInt(seqStr);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max(Integer::compareTo)
            .orElse(0);

        return datePrefix + String.format("%04d", maxSeq + 1);
    }
}
