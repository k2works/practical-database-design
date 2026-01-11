package com.example.fas.domain.model.autojournal;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 自動仕訳パターンテスト.
 */
@DisplayName("自動仕訳パターン")
class AutoJournalPatternTest {

    @Nested
    @DisplayName("パターンマッチング")
    class PatternMatching {

        @Test
        @DisplayName("商品グループALLは全ての商品グループにマッチする")
        void shouldMatchAllProductGroups() {
            // Arrange
            var pattern = AutoJournalPattern.builder()
                    .patternCode("P001")
                    .productGroup("ALL")
                    .customerGroup("ALL")
                    .build();

            // Act & Assert
            assertThat(pattern.matches("加工品", "一般")).isTrue();
            assertThat(pattern.matches("生鮮品", "一般")).isTrue();
            assertThat(pattern.matches("雑貨", "特約店")).isTrue();
        }

        @Test
        @DisplayName("特定の商品グループのみにマッチする")
        void shouldMatchSpecificProductGroup() {
            // Arrange
            var pattern = AutoJournalPattern.builder()
                    .patternCode("P002")
                    .productGroup("加工品")
                    .customerGroup("ALL")
                    .build();

            // Act & Assert
            assertThat(pattern.matches("加工品", "一般")).isTrue();
            assertThat(pattern.matches("生鮮品", "一般")).isFalse();
        }

        @Test
        @DisplayName("商品グループと顧客グループの両方でマッチングする")
        void shouldMatchBothProductAndCustomerGroup() {
            // Arrange
            var pattern = AutoJournalPattern.builder()
                    .patternCode("P003")
                    .productGroup("加工品")
                    .customerGroup("特約店")
                    .build();

            // Act & Assert
            assertThat(pattern.matches("加工品", "特約店")).isTrue();
            assertThat(pattern.matches("加工品", "一般")).isFalse();
            assertThat(pattern.matches("生鮮品", "特約店")).isFalse();
        }
    }

    @Nested
    @DisplayName("有効期間チェック")
    class ValidityCheck {

        @Test
        @DisplayName("有効期間内の日付でtrueを返す")
        void shouldReturnTrueForValidDate() {
            // Arrange
            var pattern = AutoJournalPattern.builder()
                    .patternCode("P001")
                    .validFrom(LocalDate.of(2024, 1, 1))
                    .validTo(LocalDate.of(2024, 12, 31))
                    .build();

            // Act & Assert
            assertThat(pattern.isValidAt(LocalDate.of(2024, 6, 15))).isTrue();
            assertThat(pattern.isValidAt(LocalDate.of(2024, 1, 1))).isTrue();
            assertThat(pattern.isValidAt(LocalDate.of(2024, 12, 31))).isTrue();
        }

        @Test
        @DisplayName("有効期間外の日付でfalseを返す")
        void shouldReturnFalseForInvalidDate() {
            // Arrange
            var pattern = AutoJournalPattern.builder()
                    .patternCode("P001")
                    .validFrom(LocalDate.of(2024, 1, 1))
                    .validTo(LocalDate.of(2024, 12, 31))
                    .build();

            // Act & Assert
            assertThat(pattern.isValidAt(LocalDate.of(2023, 12, 31))).isFalse();
            assertThat(pattern.isValidAt(LocalDate.of(2025, 1, 1))).isFalse();
        }
    }
}
