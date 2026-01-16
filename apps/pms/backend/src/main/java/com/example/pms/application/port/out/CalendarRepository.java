package com.example.pms.application.port.out;

import com.example.pms.domain.model.calendar.WorkCalendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * カレンダーリポジトリインターフェース.
 */
public interface CalendarRepository {

    /**
     * カレンダーを保存する.
     *
     * @param calendar カレンダー
     */
    void save(WorkCalendar calendar);

    /**
     * カレンダーコードと日付で検索する.
     *
     * @param calendarCode カレンダーコード
     * @param date 日付
     * @return カレンダー
     */
    Optional<WorkCalendar> findByCalendarCodeAndDate(String calendarCode, LocalDate date);

    /**
     * 全件取得する.
     *
     * @return カレンダーリスト
     */
    List<WorkCalendar> findAll();

    /**
     * ページネーション付きで取得する.
     *
     * @param keyword 検索キーワード
     * @param limit 取得件数
     * @param offset オフセット
     * @return カレンダーリスト
     */
    List<WorkCalendar> findWithPagination(String keyword, int limit, int offset);

    /**
     * 件数を取得する.
     *
     * @param keyword 検索キーワード
     * @return 件数
     */
    long count(String keyword);

    /**
     * カレンダーを更新する.
     *
     * @param calendar カレンダー
     */
    void update(WorkCalendar calendar);

    /**
     * カレンダーコードと日付で削除する.
     *
     * @param calendarCode カレンダーコード
     * @param date 日付
     */
    void deleteByCalendarCodeAndDate(String calendarCode, LocalDate date);

    /**
     * 全件削除する.
     */
    void deleteAll();
}
