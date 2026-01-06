package com.example.fas.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.fas.application.port.out.JournalRepository;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.journal.Journal;
import com.example.fas.domain.model.journal.JournalDebitCreditDetail;
import com.example.fas.domain.model.journal.JournalDetail;
import com.example.fas.domain.model.journal.JournalVoucherType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 仕訳赤黒訂正サービステスト.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("仕訳赤黒訂正サービス")
@SuppressWarnings("PMD.TooManyStaticImports")
class JournalCorrectionServiceTest {

    @Mock
    private JournalRepository journalRepository;

    @InjectMocks
    private JournalCorrectionService journalCorrectionService;

    private Journal createTestJournal(String voucherNumber) {
        var debitDetail = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditType(DebitCreditType.DEBIT)
                .accountCode("11110")
                .amount(new BigDecimal("100000"))
                .build();

        var creditDetail = JournalDebitCreditDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .debitCreditType(DebitCreditType.CREDIT)
                .accountCode("41100")
                .amount(new BigDecimal("100000"))
                .build();

        var journalDetail = JournalDetail.builder()
                .journalVoucherNumber(voucherNumber)
                .lineNumber(1)
                .lineSummary("テスト仕訳")
                .debitCreditDetails(List.of(debitDetail, creditDetail))
                .build();

        return Journal.builder()
                .journalVoucherNumber(voucherNumber)
                .postingDate(LocalDate.of(2024, 4, 1))
                .entryDate(LocalDate.of(2024, 4, 1))
                .closingJournalFlag(false)
                .singleEntryFlag(false)
                .voucherType(JournalVoucherType.NORMAL)
                .periodicPostingFlag(false)
                .employeeCode("EMP001")
                .departmentCode("00000")
                .redSlipFlag(false)
                .details(List.of(journalDetail))
                .build();
    }

    @Nested
    @DisplayName("仕訳取消")
    class CancelJournalTests {

        @Test
        @DisplayName("仕訳を取消できる（赤伝が発行される）")
        void canCancelJournal() {
            // Arrange
            var originalJournal = createTestJournal("J0001");
            when(journalRepository.findWithDetails("J0001"))
                    .thenReturn(Optional.of(originalJournal));

            // Act
            var redSlipNumber = journalCorrectionService.cancelJournal("J0001");

            // Assert
            assertThat(redSlipNumber).isEqualTo("J0001R");

            // 赤伝が保存されていることを確認
            var journalCaptor = ArgumentCaptor.forClass(Journal.class);
            verify(journalRepository).save(journalCaptor.capture());

            var savedRedSlip = journalCaptor.getValue();
            assertThat(savedRedSlip.getJournalVoucherNumber()).isEqualTo("J0001R");
            assertThat(savedRedSlip.getRedSlipFlag()).isTrue();
            assertThat(savedRedSlip.getRedBlackVoucherNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("赤伝の貸借が反転している")
        void redSlipHasReversedDebitCredit() {
            // Arrange
            var originalJournal = createTestJournal("J0001");
            when(journalRepository.findWithDetails("J0001"))
                    .thenReturn(Optional.of(originalJournal));

            // Act
            journalCorrectionService.cancelJournal("J0001");

            // Assert
            var journalCaptor = ArgumentCaptor.forClass(Journal.class);
            verify(journalRepository).save(journalCaptor.capture());

            var savedRedSlip = journalCaptor.getValue();
            var redDetails = savedRedSlip.getDetails().get(0).getDebitCreditDetails();

            // 元の借方→貸方に反転
            var originalDebit = redDetails.stream()
                    .filter(d -> "11110".equals(d.getAccountCode()))
                    .findFirst().orElseThrow();
            assertThat(originalDebit.getDebitCreditType()).isEqualTo(DebitCreditType.CREDIT);

            // 元の貸方→借方に反転
            var originalCredit = redDetails.stream()
                    .filter(d -> "41100".equals(d.getAccountCode()))
                    .findFirst().orElseThrow();
            assertThat(originalCredit.getDebitCreditType()).isEqualTo(DebitCreditType.DEBIT);
        }

        @Test
        @DisplayName("存在しない仕訳を取消しようとするとエラー")
        void throwsExceptionWhenJournalNotFound() {
            // Arrange
            when(journalRepository.findWithDetails("J9999"))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> journalCorrectionService.cancelJournal("J9999"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("仕訳が見つかりません");
        }

        @Test
        @DisplayName("既に取消済みの仕訳を再取消しようとするとエラー")
        void throwsExceptionWhenAlreadyCancelled() {
            // Arrange
            var cancelledJournal = createTestJournal("J0001");
            cancelledJournal.setRedSlipFlag(true);
            when(journalRepository.findWithDetails("J0001"))
                    .thenReturn(Optional.of(cancelledJournal));

            // Act & Assert
            assertThatThrownBy(() -> journalCorrectionService.cancelJournal("J0001"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("既に取消済み");
        }
    }

    @Nested
    @DisplayName("仕訳赤黒訂正")
    class CorrectJournalTests {

        @BeforeEach
        void setUp() {
            var originalJournal = createTestJournal("J0001");
            when(journalRepository.findWithDetails("J0001"))
                    .thenReturn(Optional.of(originalJournal));
        }

        @Test
        @DisplayName("仕訳を赤黒訂正できる")
        void canCorrectJournal() {
            // Arrange
            var correctedJournal = createTestJournal("J0002");
            correctedJournal.getDetails().get(0).getDebitCreditDetails().get(0)
                    .setAmount(new BigDecimal("120000"));
            correctedJournal.getDetails().get(0).getDebitCreditDetails().get(1)
                    .setAmount(new BigDecimal("120000"));

            // Act
            var result = journalCorrectionService.correctJournal("J0001", correctedJournal);

            // Assert
            assertThat(result.redSlipNumber()).isEqualTo("J0001R");
            assertThat(result.blackSlipNumber()).isEqualTo("J0002");

            // 赤伝と黒伝の両方が保存されていることを確認
            verify(journalRepository, times(2)).save(any(Journal.class));
        }

        @Test
        @DisplayName("訂正仕訳の貸借が一致しない場合はエラー")
        void throwsExceptionWhenCorrectedJournalNotBalanced() {
            // Arrange
            var correctedJournal = createTestJournal("J0002");
            // 借方のみ金額を変更（貸借不一致に）
            correctedJournal.getDetails().get(0).getDebitCreditDetails().get(0)
                    .setAmount(new BigDecimal("150000"));

            // Act & Assert
            assertThatThrownBy(() ->
                    journalCorrectionService.correctJournal("J0001", correctedJournal))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("貸借が一致しません");
        }
    }
}
