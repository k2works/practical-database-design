package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.CalendarUseCase;
import com.example.pms.domain.model.calendar.DateType;
import com.example.pms.domain.model.calendar.WorkCalendar;
import com.example.pms.domain.model.common.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * カレンダーマスタ画面コントローラーテスト.
 */
@WebMvcTest(CalendarWebController.class)
@DisplayName("カレンダーマスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class CalendarWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalendarUseCase calendarUseCase;

    @Nested
    @DisplayName("GET /calendars - カレンダー一覧")
    class ListCalendars {

        @Test
        @DisplayName("カレンダー一覧画面を表示できる")
        void shouldDisplayCalendarList() throws Exception {
            WorkCalendar calendar = createTestCalendar("CAL001", LocalDate.of(2024, 1, 1), DateType.WORKING);
            PageResult<WorkCalendar> pageResult = new PageResult<>(List.of(calendar), 0, 20, 1);
            Mockito.when(calendarUseCase.getCalendars(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/calendars"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("calendars/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("calendars"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            WorkCalendar calendar = createTestCalendar("CAL001", LocalDate.of(2024, 1, 1), DateType.HOLIDAY);
            PageResult<WorkCalendar> pageResult = new PageResult<>(List.of(calendar), 0, 20, 1);
            Mockito.when(calendarUseCase.getCalendars(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("CAL001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/calendars")
                    .param("keyword", "CAL001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("calendars/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "CAL001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            WorkCalendar calendar = createTestCalendar("CAL001", LocalDate.of(2024, 1, 1), DateType.WORKING);
            PageResult<WorkCalendar> pageResult = new PageResult<>(List.of(calendar), 1, 10, 25);
            Mockito.when(calendarUseCase.getCalendars(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/calendars")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /calendars/new - カレンダー登録画面")
    class NewCalendar {

        @Test
        @DisplayName("カレンダー登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/calendars/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("calendars/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("dateTypes"));
        }
    }

    @Nested
    @DisplayName("POST /calendars - カレンダー登録処理")
    class CreateCalendar {

        @Test
        @DisplayName("カレンダーを登録できる")
        void shouldCreateCalendar() throws Exception {
            WorkCalendar created = createTestCalendar("CAL001", LocalDate.of(2024, 1, 1), DateType.WORKING);
            Mockito.when(calendarUseCase.createCalendar(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/calendars")
                    .param("calendarCode", "CAL001")
                    .param("date", "2024-01-01")
                    .param("dateType", "WORKING")
                    .param("workingHours", "8.0"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/calendars"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/calendars")
                    .param("calendarCode", "")
                    .param("date", "")
                    .param("dateType", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("calendars/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "calendarCode", "date", "dateType"));
        }
    }

    private WorkCalendar createTestCalendar(String code, LocalDate date, DateType dateType) {
        return WorkCalendar.builder()
            .calendarCode(code)
            .date(date)
            .dateType(dateType)
            .workingHours(BigDecimal.valueOf(8.0))
            .build();
    }
}
