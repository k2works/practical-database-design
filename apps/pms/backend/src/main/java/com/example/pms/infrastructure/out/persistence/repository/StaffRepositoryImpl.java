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
}
