package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.WorkCalendarRepository;
import com.example.pms.domain.model.calendar.WorkCalendar;
import com.example.pms.infrastructure.out.persistence.mapper.WorkCalendarMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * カレンダマスタリポジトリ実装.
 */
@Repository
public class WorkCalendarRepositoryImpl implements WorkCalendarRepository {

    private final WorkCalendarMapper workCalendarMapper;

    public WorkCalendarRepositoryImpl(WorkCalendarMapper workCalendarMapper) {
        this.workCalendarMapper = workCalendarMapper;
    }

    @Override
    public void save(WorkCalendar workCalendar) {
        workCalendarMapper.insert(workCalendar);
    }

    @Override
    public Optional<WorkCalendar> findByCalendarCodeAndDate(String calendarCode, LocalDate date) {
        return workCalendarMapper.findByCalendarCodeAndDate(calendarCode, date);
    }

    @Override
    public List<WorkCalendar> findByCalendarCode(String calendarCode) {
        return workCalendarMapper.findByCalendarCode(calendarCode);
    }

    @Override
    public List<WorkCalendar> findByCalendarCodeAndDateRange(String calendarCode, LocalDate startDate, LocalDate endDate) {
        return workCalendarMapper.findByCalendarCodeAndDateRange(calendarCode, startDate, endDate);
    }

    @Override
    public void update(WorkCalendar workCalendar) {
        workCalendarMapper.update(workCalendar);
    }

    @Override
    public void deleteAll() {
        workCalendarMapper.deleteAll();
    }
}
