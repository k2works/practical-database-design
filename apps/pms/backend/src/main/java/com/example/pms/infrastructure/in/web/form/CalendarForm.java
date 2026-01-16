package com.example.pms.infrastructure.in.web.form;

import com.example.pms.application.port.in.command.CreateCalendarCommand;
import com.example.pms.application.port.in.command.UpdateCalendarCommand;
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
     * フォームから登録コマンドを生成.
     *
     * @return 登録コマンド
     */
    public CreateCalendarCommand toCreateCommand() {
        return CreateCalendarCommand.builder()
            .calendarCode(this.calendarCode)
            .date(this.date)
            .dateType(this.dateType)
            .workingHours(this.workingHours)
            .note(this.note)
            .build();
    }

    /**
     * フォームから更新コマンドを生成.
     *
     * @return 更新コマンド
     */
    public UpdateCalendarCommand toUpdateCommand() {
        return UpdateCalendarCommand.builder()
            .dateType(this.dateType)
            .workingHours(this.workingHours)
            .note(this.note)
            .build();
    }

    /**
     * フォームからエンティティを生成.
     *
     * @return カレンダーエンティティ
     * @deprecated Use {@link #toCreateCommand()} or {@link #toUpdateCommand()} instead
     */
    @Deprecated
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
