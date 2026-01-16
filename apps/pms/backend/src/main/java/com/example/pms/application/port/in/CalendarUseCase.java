package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreateCalendarCommand;
import com.example.pms.application.port.in.command.UpdateCalendarCommand;
import com.example.pms.domain.model.calendar.WorkCalendar;
import com.example.pms.domain.model.common.PageResult;

import java.util.List;

/**
 * カレンダーユースケースインターフェース（Input Port）.
 */
public interface CalendarUseCase {

    /**
     * カレンダー一覧をページネーション付きで取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード（カレンダーコード）
     * @return ページ結果
     */
    PageResult<WorkCalendar> getCalendars(int page, int size, String keyword);

    /**
     * すべてのカレンダーを取得する.
     *
     * @return カレンダーリスト
     */
    List<WorkCalendar> getAllCalendars();

    /**
     * カレンダーを登録する.
     *
     * @param command 登録コマンド
     * @return 登録したカレンダー
     */
    WorkCalendar createCalendar(CreateCalendarCommand command);

    /**
     * カレンダーを取得する.
     *
     * @param calendarCode カレンダーコード
     * @param date 日付
     * @return カレンダー
     */
    java.util.Optional<WorkCalendar> getCalendar(String calendarCode, java.time.LocalDate date);

    /**
     * カレンダーを更新する.
     *
     * @param calendarCode カレンダーコード
     * @param date 日付
     * @param command 更新コマンド
     * @return 更新したカレンダー
     */
    WorkCalendar updateCalendar(String calendarCode, java.time.LocalDate date, UpdateCalendarCommand command);

    /**
     * カレンダーを削除する.
     *
     * @param calendarCode カレンダーコード
     * @param date 日付
     */
    void deleteCalendar(String calendarCode, java.time.LocalDate date);
}
