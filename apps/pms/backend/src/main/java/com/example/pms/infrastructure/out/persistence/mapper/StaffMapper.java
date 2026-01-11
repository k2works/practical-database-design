package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.staff.Staff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface StaffMapper {
    void insert(Staff staff);
    Optional<Staff> findByStaffCode(String staffCode);
    Optional<Staff> findByStaffCodeAndDate(@Param("staffCode") String staffCode,
                                            @Param("baseDate") LocalDate baseDate);
    List<Staff> findByDepartmentCode(String departmentCode);
    List<Staff> findAll();
    void update(Staff staff);
    void deleteAll();
}
