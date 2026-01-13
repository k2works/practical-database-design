package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.calendar.WorkCalendar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface CalendarMapper {
    void insert(WorkCalendar calendar);
    Optional<WorkCalendar> findByCalendarCodeAndDate(@Param("calendarCode") String calendarCode,
                                                      @Param("date") LocalDate date);
    List<WorkCalendar> findAll();
    List<WorkCalendar> findWithPagination(@Param("keyword") String keyword,
                                           @Param("limit") int limit,
                                           @Param("offset") int offset);
    long count(@Param("keyword") String keyword);
    void update(WorkCalendar calendar);
    void deleteByCalendarCodeAndDate(@Param("calendarCode") String calendarCode,
                                      @Param("date") LocalDate date);
    void deleteAll();
}
