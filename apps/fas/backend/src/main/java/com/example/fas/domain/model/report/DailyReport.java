package com.example.fas.domain.model.report;

import com.example.fas.domain.model.balance.DailyReportLine;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日計表を表すドメインモデル.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyReport {

    private LocalDate date;
    private List<DailyReportLine> lines;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;

    /**
     * 明細行から合計を計算してインスタンスを生成.
     *
     * @param date 対象日
     * @param lines 明細行リスト
     * @return 日計表インスタンス
     */
    @SuppressWarnings("PMD.ShortMethodName")
    public static DailyReport of(LocalDate date, List<DailyReportLine> lines) {
        BigDecimal totalDebit = lines.stream()
                .map(DailyReportLine::getDebitTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = lines.stream()
                .map(DailyReportLine::getCreditTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DailyReport.builder()
                .date(date)
                .lines(lines)
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .build();
    }

    /**
     * 貸借が一致しているか確認.
     *
     * @return 一致していればtrue
     */
    public boolean isBalanced() {
        return totalDebit.compareTo(totalCredit) == 0;
    }
}
