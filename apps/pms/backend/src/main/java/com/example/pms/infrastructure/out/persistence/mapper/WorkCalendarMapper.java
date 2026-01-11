package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.calendar.WorkCalendar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface WorkCalendarMapper {
    void insert(WorkCalendar workCalendar);
    Optional<WorkCalendar> findByCalendarCodeAndDate(@Param("calendarCode") String calendarCode,
                                                      @Param("date") LocalDate date);
    List<WorkCalendar> findByCalendarCode(String calendarCode);
    List<WorkCalendar> findByCalendarCodeAndDateRange(@Param("calendarCode") String calendarCode,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
    void update(WorkCalendar workCalendar);
    void deleteAll();
}
