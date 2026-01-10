package com.example.fas.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.fas.application.port.in.command.CreateJournalCommand;
import com.example.fas.application.port.in.command.CreateJournalCommand.DebitCreditCommand;
import com.example.fas.application.port.in.command.CreateJournalCommand.JournalDetailCommand;
import com.example.fas.application.port.in.dto.JournalResponse;
import com.example.fas.application.port.out.JournalRepository;
import com.example.fas.domain.exception.JournalBalanceException;
import com.example.fas.domain.exception.JournalNotFoundException;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.journal.Journal;
import com.example.fas.domain.model.journal.JournalDebitCreditDetail;
import com.example.fas.domain.model.journal.JournalDetail;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * JournalApplicationService のユニットテスト.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("仕訳アプリケーションサービス")
@SuppressWarnings("PMD.TooManyStaticImports")
class JournalApplicationServiceTest {

    @Mock
    private JournalRepository journalRepository;

    @InjectMocks
    private JournalApplicationService journalApplicationService;

    private Journal testJournal;

    @BeforeEach
    void setUp() {
        JournalDebitCreditDetail debitDetail = JournalDebitCreditDetail.builder()
                .journalVoucherNumber("J00001")
                .lineNumber(1)
                .debitCreditType(DebitCreditType.DEBIT)
                .accountCode("11110")
                .amount(new BigDecimal("10000"))
                .build();

        JournalDebitCreditDetail creditDetail = JournalDebitCreditDetail.builder()
                .journalVoucherNumber("J00001")
                .lineNumber(1)
                .debitCreditType(DebitCreditType.CREDIT)
                .accountCode("21110")
                .amount(new BigDecimal("10000"))
                .build();

        JournalDetail detail = JournalDetail.builder()
                .journalVoucherNumber("J00001")
                .lineNumber(1)
                .lineSummary("テスト仕訳")
                .debitCreditDetails(List.of(debitDetail, creditDetail))
                .build();

        testJournal = Journal.builder()
                .journalVoucherNumber("J00001")
                .postingDate(LocalDate.of(2025, 1, 1))
                .entryDate(LocalDate.of(2025, 1, 1))
                .details(List.of(detail))
                .build();
    }

    @Nested
    @DisplayName("getJournal")
    class GetJournalTest {

        @Test
        @DisplayName("仕訳を取得できる")
        void canGetJournal() {
            // Given
            when(journalRepository.findWithDetails("J00001"))
                    .thenReturn(Optional.of(testJournal));

            // When
            JournalResponse response = journalApplicationService.getJournal("J00001");

            // Then
            assertThat(response.getJournalVoucherNumber()).isEqualTo("J00001");
            assertThat(response.getDebitTotal()).isEqualByComparingTo(new BigDecimal("10000"));
            assertThat(response.getCreditTotal()).isEqualByComparingTo(new BigDecimal("10000"));
        }

        @Test
        @DisplayName("存在しない仕訳は例外をスローする")
        void throwsExceptionForNonExistentJournal() {
            // Given
            when(journalRepository.findWithDetails("XXXXX"))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> journalApplicationService.getJournal("XXXXX"))
                    .isInstanceOf(JournalNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getJournalsByDateRange")
    class GetJournalsByDateRangeTest {

        @Test
        @DisplayName("期間指定で仕訳を取得できる")
        void canGetJournalsByDateRange() {
            // Given
            LocalDate from = LocalDate.of(2025, 1, 1);
            LocalDate to = LocalDate.of(2025, 1, 31);
            when(journalRepository.findByPostingDateBetween(from, to))
                    .thenReturn(List.of(testJournal));

            // When
            List<JournalResponse> responses =
                    journalApplicationService.getJournalsByDateRange(from, to);

            // Then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getJournalVoucherNumber()).isEqualTo("J00001");
        }
    }

    @Nested
    @DisplayName("createJournal")
    class CreateJournalTest {

        @Test
        @DisplayName("仕訳を登録できる")
        void canCreateJournal() {
            // Given
            CreateJournalCommand command = CreateJournalCommand.builder()
                    .postingDate(LocalDate.of(2025, 1, 1))
                    .details(List.of(
                            JournalDetailCommand.builder()
                                    .lineSummary("テスト")
                                    .debitCreditDetails(List.of(
                                            DebitCreditCommand.builder()
                                                    .debitCreditType("借方")
                                                    .accountCode("11110")
                                                    .amount(new BigDecimal("10000"))
                                                    .build(),
                                            DebitCreditCommand.builder()
                                                    .debitCreditType("貸方")
                                                    .accountCode("21110")
                                                    .amount(new BigDecimal("10000"))
                                                    .build()))
                                    .build()))
                    .build();

            // When
            JournalResponse response = journalApplicationService.createJournal(command);

            // Then
            assertThat(response.getPostingDate()).isEqualTo(LocalDate.of(2025, 1, 1));
            assertThat(response.getDebitTotal()).isEqualByComparingTo(new BigDecimal("10000"));
            assertThat(response.getCreditTotal()).isEqualByComparingTo(new BigDecimal("10000"));
            verify(journalRepository).save(any(Journal.class));
        }

        @Test
        @DisplayName("貸借不一致の場合は例外をスローする")
        void throwsExceptionForUnbalancedJournal() {
            // Given
            CreateJournalCommand command = CreateJournalCommand.builder()
                    .postingDate(LocalDate.of(2025, 1, 1))
                    .details(List.of(
                            JournalDetailCommand.builder()
                                    .lineSummary("テスト")
                                    .debitCreditDetails(List.of(
                                            DebitCreditCommand.builder()
                                                    .debitCreditType("借方")
                                                    .accountCode("11110")
                                                    .amount(new BigDecimal("10000"))
                                                    .build(),
                                            DebitCreditCommand.builder()
                                                    .debitCreditType("貸方")
                                                    .accountCode("21110")
                                                    .amount(new BigDecimal("5000"))
                                                    .build()))
                                    .build()))
                    .build();

            // When & Then
            assertThatThrownBy(() -> journalApplicationService.createJournal(command))
                    .isInstanceOf(JournalBalanceException.class);
        }
    }

    @Nested
    @DisplayName("deleteJournal")
    class DeleteJournalTest {

        @Test
        @DisplayName("仕訳を削除できる")
        void canDeleteJournal() {
            // Given
            when(journalRepository.findByVoucherNumber("J00001"))
                    .thenReturn(Optional.of(testJournal));

            // When
            journalApplicationService.deleteJournal("J00001");

            // Then
            verify(journalRepository).delete("J00001");
        }

        @Test
        @DisplayName("存在しない仕訳の削除は例外をスローする")
        void throwsExceptionForNonExistentJournal() {
            // Given
            when(journalRepository.findByVoucherNumber("XXXXX"))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> journalApplicationService.deleteJournal("XXXXX"))
                    .isInstanceOf(JournalNotFoundException.class);
        }
    }
}
