package com.example.pms.infrastructure.report;

import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PDF 帳票生成.
 */
@Component
@SuppressWarnings({"PMD.ConsecutiveLiteralAppends", "PMD.GodClass"})
public class PdfReportGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(PdfReportGenerator.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final int INITIAL_BUFFER_SIZE = 2048;
    private static final String FONT_FAMILY = "Japanese";

    /** Windows のシステムフォントパス. */
    private static final String[] WINDOWS_FONTS = {
        "C:/Windows/Fonts/YuGothM.ttc",
        "C:/Windows/Fonts/YuGothR.ttc",
        "C:/Windows/Fonts/msgothic.ttc",
        "C:/Windows/Fonts/meiryo.ttc"
    };

    /** macOS のシステムフォントパス. */
    private static final String[] MAC_FONTS = {
        "/System/Library/Fonts/ヒラギノ角ゴシック W3.ttc",
        "/System/Library/Fonts/Hiragino Sans GB.ttc",
        "/Library/Fonts/Arial Unicode.ttf"
    };

    private final File japaneseFontFile;

    /**
     * コンストラクタ.
     */
    public PdfReportGenerator() {
        this.japaneseFontFile = findJapaneseFont();
    }

    /**
     * 発注一覧 PDF を生成する.
     *
     * @param orders 発注リスト
     * @return PDF バイト配列
     */
    public byte[] generatePurchaseOrderList(List<PurchaseOrder> orders) {
        String html = buildPurchaseOrderListHtml(orders);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder()
                .useFastMode();

            if (japaneseFontFile != null) {
                builder = builder.useFont(japaneseFontFile, FONT_FAMILY);
            }

            builder.withHtmlContent(html, null)
                .toStream(out)
                .run();

            return out.toByteArray();

        } catch (IOException e) {
            throw new ReportGenerationException("PDF 生成に失敗しました", e);
        }
    }

    /**
     * 発注書 PDF を生成する.
     *
     * @param order 発注
     * @return PDF バイト配列
     */
    public byte[] generatePurchaseOrderPdf(PurchaseOrder order) {
        String html = buildPurchaseOrderHtml(order);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder()
                .useFastMode();

            if (japaneseFontFile != null) {
                builder = builder.useFont(japaneseFontFile, FONT_FAMILY);
            }

            builder.withHtmlContent(html, null)
                .toStream(out)
                .run();

            return out.toByteArray();

        } catch (IOException e) {
            throw new ReportGenerationException("PDF 生成に失敗しました", e);
        }
    }

    private String buildPurchaseOrderHtml(PurchaseOrder order) {
        StringBuilder html = new StringBuilder(INITIAL_BUFFER_SIZE);

        appendPurchaseOrderHeader(html);
        appendPurchaseOrderTitle(html);
        appendPurchaseOrderInfo(html, order);
        appendPurchaseOrderDetailHeader(html);
        appendPurchaseOrderDetailBody(html, order);
        appendPurchaseOrderDetailFooter(html, order);
        appendHtmlFooter(html);

        return html.toString();
    }

    private void appendPurchaseOrderHeader(StringBuilder html) {
        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"/><style>")
            .append("body { font-family: Japanese, sans-serif; font-size: 10pt; margin: 20px; }")
            .append("h1 { font-size: 18pt; text-align: center; margin-bottom: 30px; }")
            .append(".info { margin-bottom: 20px; }")
            .append(".info-row { display: flex; margin-bottom: 8px; }")
            .append(".info-label { width: 120px; font-weight: bold; }")
            .append(".info-value { flex: 1; }")
            .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
            .append("th, td { border: 1px solid #333; padding: 8px; }")
            .append("th { background-color: #f0f0f0; font-weight: bold; text-align: center; }")
            .append("td.number { text-align: right; }")
            .append(".total-row { background-color: #e8e8e8; font-weight: bold; }")
            .append(".footer { margin-top: 30px; text-align: right; font-size: 9pt; color: #666; }")
            .append("</style></head><body>");
    }

    private void appendPurchaseOrderTitle(StringBuilder html) {
        html.append("<h1>発 注 書</h1>");
    }

    private void appendPurchaseOrderInfo(StringBuilder html, PurchaseOrder order) {
        html.append("<div class=\"info\">")
            .append("<div class=\"info-row\"><span class=\"info-label\">発注番号:</span>")
            .append("<span class=\"info-value\">").append(escapeHtml(order.getPurchaseOrderNumber())).append("</span></div>")
            .append("<div class=\"info-row\"><span class=\"info-label\">発注日:</span>")
            .append("<span class=\"info-value\">").append(formatDate(order.getOrderDate())).append("</span></div>")
            .append("<div class=\"info-row\"><span class=\"info-label\">仕入先:</span>")
            .append("<span class=\"info-value\">").append(getSupplierName(order)).append("</span></div>");

        if (order.getRemarks() != null && !order.getRemarks().isEmpty()) {
            html.append("<div class=\"info-row\"><span class=\"info-label\">備考:</span>")
                .append("<span class=\"info-value\">").append(escapeHtml(order.getRemarks())).append("</span></div>");
        }

        html.append("</div>");
    }

    private void appendPurchaseOrderDetailHeader(StringBuilder html) {
        html.append("<table><thead><tr>")
            .append("<th style=\"width: 40px;\">No</th>")
            .append("<th>品目コード</th>")
            .append("<th>納入予定日</th>")
            .append("<th style=\"width: 80px;\">数量</th>")
            .append("<th style=\"width: 100px;\">単価</th>")
            .append("<th style=\"width: 120px;\">金額</th>")
            .append("</tr></thead><tbody>");
    }

    private void appendPurchaseOrderDetailBody(StringBuilder html, PurchaseOrder order) {
        if (order.getDetails() != null) {
            for (var detail : order.getDetails()) {
                appendPurchaseOrderDetailRow(html, detail);
            }
        }
    }

    private void appendPurchaseOrderDetailRow(StringBuilder html, PurchaseOrderDetail detail) {
        var amount = calculateAmount(detail);
        html.append("<tr>")
            .append("<td class=\"number\">").append(detail.getLineNumber()).append("</td>")
            .append("<td>").append(escapeHtml(detail.getItemCode())).append("</td>")
            .append("<td>").append(formatDate(detail.getExpectedReceivingDate())).append("</td>")
            .append("<td class=\"number\">").append(formatDecimal(detail.getOrderQuantity())).append("</td>")
            .append("<td class=\"number\">").append(formatDecimal(detail.getOrderUnitPrice())).append("</td>")
            .append("<td class=\"number\">").append(formatDecimal(amount)).append("</td>")
            .append("</tr>");
    }

    private void appendPurchaseOrderDetailFooter(StringBuilder html, PurchaseOrder order) {
        var total = calculateTotal(order);
        html.append("</tbody><tfoot>")
            .append("<tr class=\"total-row\">")
            .append("<td colspan=\"5\" style=\"text-align: right;\">合計</td>")
            .append("<td class=\"number\">").append(formatDecimal(total)).append("</td>")
            .append("</tr></tfoot></table>")
            .append("<div class=\"footer\">出力日: ").append(LocalDate.now().format(DATE_FORMATTER)).append("</div>");
    }

    private BigDecimal calculateAmount(PurchaseOrderDetail detail) {
        if (detail.getOrderQuantity() == null || detail.getOrderUnitPrice() == null) {
            return BigDecimal.ZERO;
        }
        return detail.getOrderQuantity().multiply(detail.getOrderUnitPrice());
    }

    private BigDecimal calculateTotal(PurchaseOrder order) {
        if (order.getDetails() == null) {
            return BigDecimal.ZERO;
        }
        return order.getDetails().stream()
            .map(this::calculateAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return String.format("%,.2f", value);
    }

    private String buildPurchaseOrderListHtml(List<PurchaseOrder> orders) {
        StringBuilder html = new StringBuilder(INITIAL_BUFFER_SIZE);

        appendHtmlHeader(html);
        appendTitle(html);
        appendTableHeader(html);
        appendTableBody(html, orders);
        appendTableFooter(html, orders.size());
        appendHtmlFooter(html);

        return html.toString();
    }

    private void appendHtmlHeader(StringBuilder html) {
        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"/><style>")
            .append("body { font-family: Japanese, sans-serif; font-size: 12px; }")
            .append("h1 { font-size: 18px; margin-bottom: 10px; }")
            .append(".date { font-size: 10px; color: #666; margin-bottom: 20px; }")
            .append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }")
            .append("th, td { border: 1px solid #333; padding: 6px 8px; text-align: left; }")
            .append("th { background-color: #f0f0f0; font-weight: bold; }")
            .append(".total { margin-top: 10px; font-weight: bold; }")
            .append("</style></head><body>");
    }

    private void appendTitle(StringBuilder html) {
        html.append("<h1>発注一覧</h1>")
            .append("<div class=\"date\">出力日: ")
            .append(LocalDate.now().format(DATE_FORMATTER))
            .append("</div>");
    }

    private void appendTableHeader(StringBuilder html) {
        html.append("<table><thead><tr>")
            .append("<th>発注番号</th><th>発注日</th><th>仕入先コード</th>")
            .append("<th>仕入先名</th><th>ステータス</th><th>備考</th>")
            .append("</tr></thead><tbody>");
    }

    private void appendTableBody(StringBuilder html, List<PurchaseOrder> orders) {
        for (PurchaseOrder order : orders) {
            appendOrderRow(html, order);
        }
    }

    private void appendOrderRow(StringBuilder html, PurchaseOrder order) {
        html.append("<tr>")
            .append("<td>").append(escapeHtml(order.getPurchaseOrderNumber())).append("</td>")
            .append("<td>").append(formatDate(order.getOrderDate())).append("</td>")
            .append("<td>").append(escapeHtml(order.getSupplierCode())).append("</td>")
            .append("<td>").append(getSupplierName(order)).append("</td>")
            .append("<td>").append(getStatusName(order)).append("</td>")
            .append("<td>").append(escapeHtml(order.getRemarks())).append("</td>")
            .append("</tr>");
    }

    private void appendTableFooter(StringBuilder html, int count) {
        html.append("</tbody></table>")
            .append("<div class=\"total\">合計: ")
            .append(count)
            .append(" 件</div>");
    }

    private void appendHtmlFooter(StringBuilder html) {
        html.append("</body></html>");
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    private String getSupplierName(PurchaseOrder order) {
        return order.getSupplier() != null ? escapeHtml(order.getSupplier().getSupplierName()) : "";
    }

    private String getStatusName(PurchaseOrder order) {
        return order.getStatus() != null ? order.getStatus().getDisplayName() : "";
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }

    /**
     * 日本語フォントを検索する.
     */
    private File findJapaneseFont() {
        // Windows のシステムフォントを検索
        File windowsFont = findExistingFont(WINDOWS_FONTS);
        if (windowsFont != null) {
            return windowsFont;
        }

        // macOS のシステムフォントを検索
        File macFont = findExistingFont(MAC_FONTS);
        if (macFont != null) {
            return macFont;
        }

        LOG.warn("日本語フォントが見つかりません。PDF で日本語が正しく表示されない可能性があります。");
        return null;
    }

    /**
     * 存在するフォントファイルを検索する.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private File findExistingFont(String... fontPaths) {
        for (String fontPath : fontPaths) {
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("システムフォントを使用: {}", fontPath);
                }
                return fontFile;
            }
        }
        return null;
    }
}
