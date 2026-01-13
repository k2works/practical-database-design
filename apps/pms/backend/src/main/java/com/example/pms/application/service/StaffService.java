package com.example.pms.application.service;

import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.application.port.out.StaffRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.staff.Staff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 担当者サービス（Application Service）.
 */
@Service
@Transactional
public class StaffService implements StaffUseCase {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Staff> getStaffList(int page, int size, String keyword) {
        int offset = page * size;
        List<Staff> staffList = staffRepository.findWithPagination(keyword, size, offset);
        long totalElements = staffRepository.count(keyword);
        return new PageResult<>(staffList, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> getStaff(String staffCode, LocalDate effectiveFrom) {
        return staffRepository.findByStaffCodeAndEffectiveFrom(staffCode, effectiveFrom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    @Override
    public Staff createStaff(Staff staff) {
        staffRepository.save(staff);
        return staff;
    }

    @Override
    public Staff updateStaff(String staffCode, LocalDate effectiveFrom, Staff staff) {
        staffRepository.update(staff);
        return staff;
    }

    @Override
    public void deleteStaff(String staffCode, LocalDate effectiveFrom) {
        staffRepository.deleteByStaffCodeAndEffectiveFrom(staffCode, effectiveFrom);
    }
}
