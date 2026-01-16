package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.CalendarRepository;
import com.example.pms.domain.model.calendar.WorkCalendar;
import com.example.pms.infrastructure.out.persistence.mapper.CalendarMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * カレンダーリポジトリ実装.
 */
@Repository
public class CalendarRepositoryImpl implements CalendarRepository {

    private final CalendarMapper calendarMapper;

    public CalendarRepositoryImpl(CalendarMapper calendarMapper) {
        this.calendarMapper = calendarMapper;
    }

    @Override
    public void save(WorkCalendar calendar) {
        calendarMapper.insert(calendar);
    }

    @Override
    public Optional<WorkCalendar> findByCalendarCodeAndDate(String calendarCode, LocalDate date) {
        return calendarMapper.findByCalendarCodeAndDate(calendarCode, date);
    }

    @Override
    public List<WorkCalendar> findAll() {
        return calendarMapper.findAll();
    }

    @Override
    public List<WorkCalendar> findWithPagination(String keyword, int limit, int offset) {
        return calendarMapper.findWithPagination(keyword, limit, offset);
    }

    @Override
    public long count(String keyword) {
        return calendarMapper.count(keyword);
    }

    @Override
    public void update(WorkCalendar calendar) {
        calendarMapper.update(calendar);
    }

    @Override
    public void deleteByCalendarCodeAndDate(String calendarCode, LocalDate date) {
        calendarMapper.deleteByCalendarCodeAndDate(calendarCode, date);
    }

    @Override
    public void deleteAll() {
        calendarMapper.deleteAll();
    }
}
