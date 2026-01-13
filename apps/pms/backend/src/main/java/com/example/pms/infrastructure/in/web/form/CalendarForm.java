package com.example.pms.infrastructure.in.web.form;

import com.example.pms.domain.model.calendar.DateType;
import com.example.pms.domain.model.calendar.WorkCalendar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * カレンダー登録フォーム.
 */
@Data
public class CalendarForm {

    @NotBlank(message = "カレンダーコードは必須です")
    @Size(max = 20, message = "カレンダーコードは20文字以内で入力してください")
    private String calendarCode;

    @NotNull(message = "日付は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "日付区分は必須です")
    private DateType dateType;

    private BigDecimal workingHours;

    @Size(max = 200, message = "備考は200文字以内で入力してください")
    private String note;

    /**
     * フォームからエンティティを生成.
     *
     * @return カレンダーエンティティ
     */
    public WorkCalendar toEntity() {
        return WorkCalendar.builder()
            .calendarCode(this.calendarCode)
            .date(this.date)
            .dateType(this.dateType)
            .workingHours(this.workingHours)
            .note(this.note)
            .build();
    }
}
