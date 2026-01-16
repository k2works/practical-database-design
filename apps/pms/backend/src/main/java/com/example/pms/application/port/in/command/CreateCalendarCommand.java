package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.calendar.DateType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * カレンダー登録コマンド.
 */
@Value
@Builder
public class CreateCalendarCommand {
    String calendarCode;
    LocalDate date;
    DateType dateType;
    BigDecimal workingHours;
    String note;
}
