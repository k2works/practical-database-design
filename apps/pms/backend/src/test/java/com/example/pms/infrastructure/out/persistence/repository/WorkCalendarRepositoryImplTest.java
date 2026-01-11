package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.WorkCalendarRepository;
import com.example.pms.domain.model.calendar.DateType;
import com.example.pms.domain.model.calendar.WorkCalendar;
import com.example.pms.testsetup.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * カレンダマスタリポジトリテスト.
 */
@DisplayName("カレンダマスタリポジトリ")
class WorkCalendarRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private WorkCalendarRepository workCalendarRepository;

    @BeforeEach
    void setUp() {
        workCalendarRepository.deleteAll();
    }

    private WorkCalendar createWorkCalendar(String code, LocalDate date, DateType dateType, BigDecimal hours) {
        return WorkCalendar.builder()
                .calendarCode(code)
                .date(date)
                .dateType(dateType)
                .workingHours(hours)
                .build();
    }

    @Nested
    @DisplayName("登録")
    class Registration {

        @Test
        @DisplayName("カレンダを登録できる")
        void canRegisterCalendar() {
            // Arrange
            WorkCalendar calendar = createWorkCalendar("CAL001", LocalDate.of(2024, 1, 1),
                    DateType.WORKING, new BigDecimal("8.00"));

            // Act
            workCalendarRepository.save(calendar);

            // Assert
            Optional<WorkCalendar> found = workCalendarRepository.findByCalendarCodeAndDate(
                    "CAL001", LocalDate.of(2024, 1, 1));
            assertThat(found).isPresent();
            assertThat(found.get().getDateType()).isEqualTo(DateType.WORKING);
            assertThat(found.get().getWorkingHours()).isEqualByComparingTo(new BigDecimal("8.00"));
        }

        @Test
        @DisplayName("各日付区分を登録できる")
        void canRegisterAllDateTypes() {
            // Arrange & Act & Assert
            LocalDate baseDate = LocalDate.of(2024, 1, 1);
            int dayOffset = 0;
            for (DateType dateType : DateType.values()) {
                LocalDate date = baseDate.plusDays(dayOffset++);
                WorkCalendar calendar = createWorkCalendar("CAL001", date, dateType,
                        dateType == DateType.HOLIDAY ? BigDecimal.ZERO : new BigDecimal("8.00"));
                workCalendarRepository.save(calendar);

                Optional<WorkCalendar> found = workCalendarRepository.findByCalendarCodeAndDate("CAL001", date);
                assertThat(found).isPresent();
                assertThat(found.get().getDateType()).isEqualTo(dateType);
            }
        }
    }

    @Nested
    @DisplayName("検索")
    class Search {

        @BeforeEach
        void setUpData() {
            // CAL001: 2024年1月の1週間分
            for (int i = 1; i <= 7; i++) {
                LocalDate date = LocalDate.of(2024, 1, i);
                DateType dateType = (i == 6 || i == 7) ? DateType.HOLIDAY : DateType.WORKING;
                BigDecimal hours = dateType == DateType.HOLIDAY ? BigDecimal.ZERO : new BigDecimal("8.00");
                workCalendarRepository.save(createWorkCalendar("CAL001", date, dateType, hours));
            }
            // CAL002: 別カレンダ
            workCalendarRepository.save(createWorkCalendar("CAL002", LocalDate.of(2024, 1, 1),
                    DateType.WORKING, new BigDecimal("7.50")));
        }

        @Test
        @DisplayName("カレンダコードと日付で検索できる")
        void canFindByCalendarCodeAndDate() {
            // Act
            Optional<WorkCalendar> found = workCalendarRepository.findByCalendarCodeAndDate(
                    "CAL001", LocalDate.of(2024, 1, 3));

            // Assert
            assertThat(found).isPresent();
            assertThat(found.get().getDateType()).isEqualTo(DateType.WORKING);
        }

        @Test
        @DisplayName("カレンダコードで検索できる")
        void canFindByCalendarCode() {
            // Act
            List<WorkCalendar> found = workCalendarRepository.findByCalendarCode("CAL001");

            // Assert
            assertThat(found).hasSize(7);
        }

        @Test
        @DisplayName("カレンダコードと期間で検索できる")
        void canFindByCalendarCodeAndDateRange() {
            // Act
            List<WorkCalendar> found = workCalendarRepository.findByCalendarCodeAndDateRange(
                    "CAL001", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5));

            // Assert
            assertThat(found).hasSize(5);
        }

        @Test
        @DisplayName("存在しないコードと日付で検索すると空を返す")
        void returnsEmptyForNonExistent() {
            // Act
            Optional<WorkCalendar> found = workCalendarRepository.findByCalendarCodeAndDate(
                    "NOTEXIST", LocalDate.of(2024, 1, 1));

            // Assert
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("更新")
    class Update {

        @Test
        @DisplayName("カレンダを更新できる")
        void canUpdateCalendar() {
            // Arrange
            WorkCalendar calendar = createWorkCalendar("CAL001", LocalDate.of(2024, 1, 1),
                    DateType.WORKING, new BigDecimal("8.00"));
            workCalendarRepository.save(calendar);

            // Act
            Optional<WorkCalendar> saved = workCalendarRepository.findByCalendarCodeAndDate(
                    "CAL001", LocalDate.of(2024, 1, 1));
            assertThat(saved).isPresent();
            WorkCalendar toUpdate = saved.get();
            toUpdate.setDateType(DateType.HALF_DAY);
            toUpdate.setWorkingHours(new BigDecimal("4.00"));
            toUpdate.setNote("午後休");
            workCalendarRepository.update(toUpdate);

            // Assert
            Optional<WorkCalendar> updated = workCalendarRepository.findByCalendarCodeAndDate(
                    "CAL001", LocalDate.of(2024, 1, 1));
            assertThat(updated).isPresent();
            assertThat(updated.get().getDateType()).isEqualTo(DateType.HALF_DAY);
            assertThat(updated.get().getWorkingHours()).isEqualByComparingTo(new BigDecimal("4.00"));
            assertThat(updated.get().getNote()).isEqualTo("午後休");
        }
    }
}
