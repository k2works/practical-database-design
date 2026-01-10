package com.example.fas.infrastructure.in.web.form;

import com.example.fas.application.port.in.command.CreateJournalCommand;
import com.example.fas.application.port.in.command.CreateJournalCommand.JournalDetailCommand;
import com.example.fas.application.port.in.dto.JournalResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 仕訳入力フォーム.
 */
@Data
public class JournalForm {

    @NotNull(message = "起票日は必須です")
    private LocalDate postingDate;

    private String voucherType;

    private Boolean closingJournalFlag;

    private Boolean singleEntryFlag;

    private String departmentCode;

    private String employeeCode;

    private String lineSummary;

    @Valid
    @NotEmpty(message = "仕訳明細は1行以上必要です")
    private List<JournalLineForm> lines = new ArrayList<>();

    /**
     * デフォルトコンストラクタ.
     */
    public JournalForm() {
        this.postingDate = LocalDate.now();
        this.voucherType = "NORMAL";
        this.closingJournalFlag = false;
        this.singleEntryFlag = false;
    }

    /**
     * フォームをコマンドに変換.
     *
     * @return 仕訳登録コマンド
     */
    public CreateJournalCommand toCreateCommand() {
        // 行ごとにグループ化して JournalDetailCommand を作成
        // 現在は1行=1明細として処理
        List<JournalDetailCommand> details = new ArrayList<>();

        for (JournalLineForm line : lines) {
            details.add(new JournalDetailCommand(
                    this.lineSummary,
                    List.of(line.toCommand())
            ));
        }

        return new CreateJournalCommand(
                this.postingDate,
                LocalDate.now(), // entryDate
                this.voucherType,
                this.closingJournalFlag,
                this.singleEntryFlag,
                null, // periodicPostingFlag
                this.employeeCode,
                this.departmentCode,
                details
        );
    }

    /**
     * JournalResponse からフォームを生成.
     *
     * @param response 仕訳レスポンス
     * @return フォーム
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public static JournalForm from(JournalResponse response) {
        JournalForm form = new JournalForm();
        form.setPostingDate(response.getPostingDate());
        form.setVoucherType(response.getVoucherType());
        form.setClosingJournalFlag(response.getClosingJournalFlag());
        form.setSingleEntryFlag(response.getSingleEntryFlag());
        form.setDepartmentCode(response.getDepartmentCode());
        form.setEmployeeCode(response.getEmployeeCode());

        List<JournalLineForm> lines = new ArrayList<>();
        if (response.getDetails() != null) {
            for (var detail : response.getDetails()) {
                if (detail.getDebitCreditDetails() != null) {
                    for (var dcDetail : detail.getDebitCreditDetails()) {
                        JournalLineForm lineForm = new JournalLineForm();
                        lineForm.setDebitCreditType(dcDetail.getDebitCreditType());
                        lineForm.setAccountCode(dcDetail.getAccountCode());
                        lineForm.setSubAccountCode(dcDetail.getSubAccountCode());
                        lineForm.setDepartmentCode(dcDetail.getDepartmentCode());
                        lineForm.setAmount(dcDetail.getAmount());
                        lineForm.setTaxType(dcDetail.getTaxType());
                        lineForm.setTaxRate(dcDetail.getTaxRate());
                        lineForm.setLineSummary(detail.getLineSummary());
                        lines.add(lineForm);
                    }
                }
            }
        }
        form.setLines(lines);

        return form;
    }

    /**
     * 初期化（空の明細行を追加）.
     */
    public void initializeLines() {
        if (this.lines.isEmpty()) {
            // 借方1行、貸方1行を追加
            JournalLineForm debitLine = new JournalLineForm();
            debitLine.setDebitCreditType("借方");
            this.lines.add(debitLine);

            JournalLineForm creditLine = new JournalLineForm();
            creditLine.setDebitCreditType("貸方");
            this.lines.add(creditLine);
        }
    }
}
