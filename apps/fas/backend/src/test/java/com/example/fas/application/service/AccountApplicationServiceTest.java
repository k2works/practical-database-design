package com.example.fas.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.fas.application.port.in.dto.AccountResponse;
import com.example.fas.application.port.in.dto.CreateAccountCommand;
import com.example.fas.application.port.in.dto.UpdateAccountCommand;
import com.example.fas.application.port.out.AccountRepository;
import com.example.fas.domain.exception.AccountAlreadyExistsException;
import com.example.fas.domain.exception.AccountNotFoundException;
import com.example.fas.domain.model.account.Account;
import com.example.fas.domain.model.account.AggregationType;
import com.example.fas.domain.model.account.BSPLType;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.account.TransactionElementType;
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
 * AccountApplicationService のユニットテスト.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("勘定科目アプリケーションサービス")
class AccountApplicationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountApplicationService sut;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .accountCode("11110")
                .accountName("現金")
                .accountShortName("現金")
                .bsplType(BSPLType.BS)
                .debitCreditType(DebitCreditType.DEBIT)
                .transactionElementType(TransactionElementType.ASSET)
                .aggregationType(AggregationType.POSTING)
                .build();
    }

    @Nested
    @DisplayName("getAccount")
    class GetAccountTest {

        @Test
        @DisplayName("存在する勘定科目を取得できる")
        void canGetExistingAccount() {
            // Given
            given(accountRepository.findByCode("11110")).willReturn(Optional.of(testAccount));

            // When
            AccountResponse result = sut.getAccount("11110");

            // Then
            assertThat(result.getAccountCode()).isEqualTo("11110");
            assertThat(result.getAccountName()).isEqualTo("現金");
            assertThat(result.getBsPlType()).isEqualTo("BS");
        }

        @Test
        @DisplayName("存在しない勘定科目は例外をスロー")
        void throwsExceptionForNonExistent() {
            // Given
            given(accountRepository.findByCode("99999")).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> sut.getAccount("99999"))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessageContaining("99999");
        }
    }

    @Nested
    @DisplayName("getAllAccounts")
    class GetAllAccountsTest {

        @Test
        @DisplayName("全勘定科目を取得できる")
        void canGetAllAccounts() {
            // Given
            given(accountRepository.findAll()).willReturn(List.of(testAccount));

            // When
            List<AccountResponse> result = sut.getAllAccounts();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAccountCode()).isEqualTo("11110");
        }
    }

    @Nested
    @DisplayName("getAccountsByBsPlType")
    class GetAccountsByBsPlTypeTest {

        @Test
        @DisplayName("BS区分の勘定科目を取得できる")
        void canGetBsAccounts() {
            // Given
            given(accountRepository.findByBSPLType(BSPLType.BS)).willReturn(List.of(testAccount));

            // When
            List<AccountResponse> result = sut.getAccountsByBsPlType("BS");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getBsPlType()).isEqualTo("BS");
        }
    }

    @Nested
    @DisplayName("createAccount")
    class CreateAccountTest {

        @Test
        @DisplayName("新規勘定科目を登録できる")
        void canCreateAccount() {
            // Given
            CreateAccountCommand command = CreateAccountCommand.builder()
                    .accountCode("11150")
                    .accountName("手許現金")
                    .accountShortName("手許現金")
                    .bsPlType("BS")
                    .dcType("借方")
                    .elementType("資産")
                    .summaryType("計上科目")
                    .build();
            given(accountRepository.findByCode("11150")).willReturn(Optional.empty());

            // When
            AccountResponse result = sut.createAccount(command);

            // Then
            assertThat(result.getAccountCode()).isEqualTo("11150");
            assertThat(result.getAccountName()).isEqualTo("手許現金");
            verify(accountRepository).save(any(Account.class));
        }

        @Test
        @DisplayName("既存の勘定科目コードで登録すると例外をスロー")
        void throwsExceptionForDuplicateCode() {
            // Given
            CreateAccountCommand command = CreateAccountCommand.builder()
                    .accountCode("11110")
                    .accountName("テスト科目")
                    .bsPlType("BS")
                    .dcType("借方")
                    .elementType("資産")
                    .summaryType("計上科目")
                    .build();
            given(accountRepository.findByCode("11110")).willReturn(Optional.of(testAccount));

            // When & Then
            assertThatThrownBy(() -> sut.createAccount(command))
                    .isInstanceOf(AccountAlreadyExistsException.class)
                    .hasMessageContaining("11110");
        }
    }

    @Nested
    @DisplayName("updateAccount")
    class UpdateAccountTest {

        @Test
        @DisplayName("勘定科目を更新できる")
        void canUpdateAccount() {
            // Given
            UpdateAccountCommand command = UpdateAccountCommand.builder()
                    .accountName("現金(更新)")
                    .build();
            given(accountRepository.findByCode("11110")).willReturn(Optional.of(testAccount));

            // When
            AccountResponse result = sut.updateAccount("11110", command);

            // Then
            assertThat(result.getAccountName()).isEqualTo("現金(更新)");
            verify(accountRepository).update(any(Account.class));
        }

        @Test
        @DisplayName("存在しない勘定科目の更新は例外をスロー")
        void throwsExceptionForNonExistent() {
            // Given
            UpdateAccountCommand command = UpdateAccountCommand.builder()
                    .accountName("テスト")
                    .build();
            given(accountRepository.findByCode("99999")).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> sut.updateAccount("99999", command))
                    .isInstanceOf(AccountNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteAccount")
    class DeleteAccountTest {

        @Test
        @DisplayName("勘定科目を削除できる")
        void canDeleteAccount() {
            // Given
            given(accountRepository.findByCode("11110")).willReturn(Optional.of(testAccount));

            // When
            sut.deleteAccount("11110");

            // Then
            verify(accountRepository).findByCode("11110");
        }

        @Test
        @DisplayName("存在しない勘定科目の削除は例外をスロー")
        void throwsExceptionForNonExistent() {
            // Given
            given(accountRepository.findByCode("99999")).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> sut.deleteAccount("99999"))
                    .isInstanceOf(AccountNotFoundException.class);
        }
    }
}
