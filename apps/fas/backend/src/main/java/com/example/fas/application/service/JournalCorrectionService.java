package com.example.fas.application.service;

import com.example.fas.application.port.out.JournalRepository;
import com.example.fas.domain.model.account.DebitCreditType;
import com.example.fas.domain.model.journal.Journal;
import com.example.fas.domain.model.journal.JournalDebitCreditDetail;
import com.example.fas.domain.model.journal.JournalDetail;
import java.time.LocalDate;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 仕訳赤黒訂正サービス.
 * 赤伝（取消伝票）と黒伝（訂正伝票）による仕訳訂正を行う.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JournalCorrectionService {

    private final JournalRepository journalRepository;

    /**
     * 仕訳を赤黒訂正する.
     *
     * @param originalVoucherNumber 元伝票番号
     * @param correctedJournal 訂正後の仕訳（黒伝）
     * @return 赤伝と黒伝の仕訳伝票番号
     */
    @Transactional
    public CorrectionResult correctJournal(String originalVoucherNumber,
            Journal correctedJournal) {
        // 元伝票を取得
        var original = journalRepository.findWithDetails(originalVoucherNumber)
            .orElseThrow(() -> new IllegalArgumentException(
                "元伝票が見つかりません: " + originalVoucherNumber));

        // 赤伝を作成
        var redSlip = createRedSlip(original);
        journalRepository.save(redSlip);

        // 黒伝（訂正伝票）を登録
        if (!correctedJournal.isBalanced()) {
            throw new IllegalArgumentException("訂正仕訳の貸借が一致しません");
        }
        journalRepository.save(correctedJournal);

        if (log.isInfoEnabled()) {
            log.info("赤黒訂正完了: 元伝票={}, 赤伝={}, 黒伝={}",
                originalVoucherNumber,
                redSlip.getJournalVoucherNumber(),
                correctedJournal.getJournalVoucherNumber());
        }

        return new CorrectionResult(
            redSlip.getJournalVoucherNumber(),
            correctedJournal.getJournalVoucherNumber()
        );
    }

    /**
     * 仕訳を取消する（赤伝のみ発行）.
     *
     * @param voucherNumber 取消対象の仕訳伝票番号
     * @return 赤伝の仕訳伝票番号
     */
    @Transactional
    public String cancelJournal(String voucherNumber) {
        var original = journalRepository.findWithDetails(voucherNumber)
            .orElseThrow(() -> new IllegalArgumentException(
                "仕訳が見つかりません: " + voucherNumber));

        // 既に赤伝が発行されているか確認
        if (original.getRedSlipFlag() != null && original.getRedSlipFlag()) {
            throw new IllegalStateException("この仕訳は既に取消済みです");
        }

        // 赤伝を作成
        var redSlip = createRedSlip(original);
        journalRepository.save(redSlip);

        if (log.isInfoEnabled()) {
            log.info("仕訳取消完了: 元伝票={}, 赤伝={}",
                voucherNumber, redSlip.getJournalVoucherNumber());
        }

        return redSlip.getJournalVoucherNumber();
    }

    /**
     * 赤伝を作成する.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private Journal createRedSlip(Journal original) {
        // 赤伝の仕訳伝票番号を生成（元伝票番号 + R）
        var redVoucherNumber = original.getJournalVoucherNumber() + "R";

        // 貸借を反転した明細を作成
        var redDetails = new ArrayList<JournalDetail>();
        for (var detail : original.getDetails()) {
            var reversedDCDetails = new ArrayList<JournalDebitCreditDetail>();
            for (var dcDetail : detail.getDebitCreditDetails()) {
                var reversed = JournalDebitCreditDetail.builder()
                    .journalVoucherNumber(redVoucherNumber)
                    .lineNumber(dcDetail.getLineNumber())
                    // 借方→貸方、貸方→借方に反転
                    .debitCreditType(
                        dcDetail.getDebitCreditType() == DebitCreditType.DEBIT
                            ? DebitCreditType.CREDIT
                            : DebitCreditType.DEBIT)
                    .accountCode(dcDetail.getAccountCode())
                    .subAccountCode(dcDetail.getSubAccountCode())
                    .departmentCode(dcDetail.getDepartmentCode())
                    .projectCode(dcDetail.getProjectCode())
                    .amount(dcDetail.getAmount())
                    .currencyCode(dcDetail.getCurrencyCode())
                    .exchangeRate(dcDetail.getExchangeRate())
                    .baseCurrencyAmount(dcDetail.getBaseCurrencyAmount())
                    .taxType(dcDetail.getTaxType())
                    .taxRate(dcDetail.getTaxRate())
                    .taxCalcType(dcDetail.getTaxCalcType())
                    .dueDate(dcDetail.getDueDate())
                    .cashFlowFlag(dcDetail.getCashFlowFlag())
                    .segmentCode(dcDetail.getSegmentCode())
                    .counterAccountCode(dcDetail.getCounterAccountCode())
                    .counterSubAccountCode(dcDetail.getCounterSubAccountCode())
                    .tagCode(dcDetail.getTagCode())
                    .tagContent(dcDetail.getTagContent())
                    .build();
                reversedDCDetails.add(reversed);
            }

            var redDetail = JournalDetail.builder()
                .journalVoucherNumber(redVoucherNumber)
                .lineNumber(detail.getLineNumber())
                .lineSummary(detail.getLineSummary() + "（取消）")
                .debitCreditDetails(reversedDCDetails)
                .build();
            redDetails.add(redDetail);
        }

        return Journal.builder()
            .journalVoucherNumber(redVoucherNumber)
            .postingDate(LocalDate.now())
            .entryDate(LocalDate.now())
            .closingJournalFlag(original.getClosingJournalFlag())
            .singleEntryFlag(original.getSingleEntryFlag())
            .voucherType(original.getVoucherType())
            .periodicPostingFlag(false)
            .employeeCode(original.getEmployeeCode())
            .departmentCode(original.getDepartmentCode())
            .redSlipFlag(true)
            .redBlackVoucherNumber(extractNumericPart(original.getJournalVoucherNumber()))
            .details(redDetails)
            .build();
    }

    private Integer extractNumericPart(String voucherNumber) {
        return Integer.parseInt(voucherNumber.replaceAll("[^0-9]", ""));
    }

    /**
     * 訂正結果.
     *
     * @param redSlipNumber 赤伝の仕訳伝票番号
     * @param blackSlipNumber 黒伝の仕訳伝票番号
     */
    public record CorrectionResult(
        String redSlipNumber,
        String blackSlipNumber
    ) { }
}
