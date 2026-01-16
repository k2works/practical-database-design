package com.example.pms.application.port.out;

import com.example.pms.domain.model.calendar.WorkCalendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * カレンダマスタリポジトリインターフェース.
 */
public interface WorkCalendarRepository {

    /**
     * カレンダを保存する.
     *
     * @param workCalendar カレンダ
     */
    void save(WorkCalendar workCalendar);

    /**
     * カレンダコードと日付で検索する.
     *
     * @param calendarCode カレンダコード
     * @param date 日付
     * @return カレンダ
     */
    Optional<WorkCalendar> findByCalendarCodeAndDate(String calendarCode, LocalDate date);

    /**
     * カレンダコードで検索する.
     *
     * @param calendarCode カレンダコード
     * @return カレンダリスト
     */
    List<WorkCalendar> findByCalendarCode(String calendarCode);

    /**
     * カレンダコードと期間で検索する.
     *
     * @param calendarCode カレンダコード
     * @param startDate 開始日
     * @param endDate 終了日
     * @return カレンダリスト
     */
    List<WorkCalendar> findByCalendarCodeAndDateRange(String calendarCode, LocalDate startDate, LocalDate endDate);

    /**
     * カレンダを更新する.
     *
     * @param workCalendar カレンダ
     */
    void update(WorkCalendar workCalendar);

    /**
     * 全件削除する.
     */
    void deleteAll();
}
