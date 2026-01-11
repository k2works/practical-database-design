package com.example.fas.infrastructure.in.web.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * PDF生成サービス.
 * ThymeleafテンプレートからPDFを生成する.
 */
@Service
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class PdfGeneratorService {

    private static final Logger LOG = LoggerFactory.getLogger(PdfGeneratorService.class);
    private static final String FONT_FAMILY = "Japanese";

    /** Windowsのシステムフォントパス. */
    private static final String[] WINDOWS_FONTS = {
        "C:/Windows/Fonts/YuGothM.ttc",   // Yu Gothic Medium
        "C:/Windows/Fonts/YuGothR.ttc",   // Yu Gothic Regular
        "C:/Windows/Fonts/msgothic.ttc",  // MS Gothic
        "C:/Windows/Fonts/meiryo.ttc"     // Meiryo
    };

    /** macOSのシステムフォントパス. */
    private static final String[] MAC_FONTS = {
        "/System/Library/Fonts/ヒラギノ角ゴシック W3.ttc",
        "/System/Library/Fonts/Hiragino Sans GB.ttc",
        "/Library/Fonts/Arial Unicode.ttf"
    };

    private final TemplateEngine templateEngine;
    private final File japaneseFontFile;

    /**
     * コンストラクタ.
     */
    public PdfGeneratorService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        this.japaneseFontFile = findJapaneseFont();
    }

    /**
     * ThymeleafテンプレートからPDFを生成する.
     *
     * @param templateName テンプレート名（例: "reports/daily-report-pdf"）
     * @param variables テンプレート変数
     * @return PDF バイト配列
     */
    public byte[] generatePdf(String templateName, Map<String, Object> variables) {
        // ThymeleafでHTMLを生成
        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process(templateName, context);

        // HTMLをPDFに変換
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder()
                .useFastMode();

            // 日本語フォントを登録
            if (japaneseFontFile != null) {
                builder = builder.useFont(japaneseFontFile, FONT_FAMILY);
            }

            builder.withHtmlContent(html, null)
                .toStream(outputStream)
                .run();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new PdfGenerationException("PDF生成に失敗しました", e);
        }
    }

    /**
     * 日本語フォントを検索する.
     */
    private File findJapaneseFont() {
        // クラスパスからフォントを検索
        ClassPathResource fontResource = new ClassPathResource("fonts/NotoSansJP-Regular.ttf");
        if (fontResource.exists()) {
            try {
                File fontFile = fontResource.getFile();
                if (LOG.isInfoEnabled()) {
                    LOG.info("クラスパスから日本語フォントを読み込みました: {}", fontFile.getPath());
                }
                return fontFile;
            } catch (IOException e) {
                LOG.debug("クラスパスからのフォント読み込みに失敗しました");
            }
        }

        // Windowsのシステムフォントを検索
        File windowsFont = findExistingFont(WINDOWS_FONTS);
        if (windowsFont != null) {
            return windowsFont;
        }

        // macOSのシステムフォントを検索
        File macFont = findExistingFont(MAC_FONTS);
        if (macFont != null) {
            return macFont;
        }

        LOG.warn("日本語フォントが見つかりません。PDFで日本語が正しく表示されない可能性があります。");
        return null;
    }

    /**
     * 存在するフォントファイルを検索する.
     */
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

    /**
     * PDF生成例外.
     */
    @SuppressWarnings("PMD.MissingSerialVersionUID")
    public static class PdfGenerationException extends RuntimeException {
        public PdfGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
