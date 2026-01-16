package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.calendar.DateType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * カレンダー登録コマンド.
 */
public record CreateCalendarCommand(
    String calendarCode,
    LocalDate date,
    DateType dateType,
    BigDecimal workingHours,
    String note
) {
}
