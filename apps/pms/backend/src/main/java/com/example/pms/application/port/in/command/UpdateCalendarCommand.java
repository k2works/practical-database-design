package com.example.pms.application.port.in.command;

import com.example.pms.domain.model.calendar.DateType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * カレンダー更新コマンド.
 */
@Value
@Builder
public class UpdateCalendarCommand {
    DateType dateType;
    BigDecimal workingHours;
    String note;
}
