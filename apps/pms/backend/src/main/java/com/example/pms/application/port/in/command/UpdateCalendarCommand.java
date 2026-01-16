package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.calendar.DateType;

import java.math.BigDecimal;

/**
 * カレンダー更新コマンド.
 */
public record UpdateCalendarCommand(
    DateType dateType,
    BigDecimal workingHours,
    String note
) {
}
