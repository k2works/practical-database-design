package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.StaffRepository;
import com.example.pms.domain.model.staff.Staff;
import com.example.pms.infrastructure.out.persistence.mapper.StaffMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class StaffRepositoryImpl implements StaffRepository {

    private final StaffMapper staffMapper;

    public StaffRepositoryImpl(StaffMapper staffMapper) {
        this.staffMapper = staffMapper;
    }

    @Override
    public void save(Staff staff) {
        staffMapper.insert(staff);
    }

    @Override
    public Optional<Staff> findByStaffCode(String staffCode) {
        return staffMapper.findByStaffCode(staffCode);
    }

    @Override
    public Optional<Staff> findByStaffCodeAndDate(String staffCode, LocalDate baseDate) {
        return staffMapper.findByStaffCodeAndDate(staffCode, baseDate);
    }

    @Override
    public List<Staff> findByDepartmentCode(String departmentCode) {
        return staffMapper.findByDepartmentCode(departmentCode);
    }

    @Override
    public List<Staff> findAll() {
        return staffMapper.findAll();
    }

    @Override
    public void update(Staff staff) {
        staffMapper.update(staff);
    }

    @Override
    public void deleteAll() {
        staffMapper.deleteAll();
    }

    @Override
    public List<Staff> findWithPagination(String keyword, int limit, int offset) {
        return staffMapper.findWithPagination(keyword, limit, offset);
    }

    @Override
    public long count(String keyword) {
        return staffMapper.count(keyword);
    }

    @Override
    public Optional<Staff> findByStaffCodeAndEffectiveFrom(String staffCode, LocalDate effectiveFrom) {
        return staffMapper.findByStaffCodeAndEffectiveFrom(staffCode, effectiveFrom);
    }

    @Override
    public void deleteByStaffCodeAndEffectiveFrom(String staffCode, LocalDate effectiveFrom) {
        staffMapper.deleteByStaffCodeAndEffectiveFrom(staffCode, effectiveFrom);
    }
}
