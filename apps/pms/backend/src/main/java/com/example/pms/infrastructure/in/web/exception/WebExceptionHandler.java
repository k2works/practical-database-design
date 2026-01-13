package com.example.pms.infrastructure.in.web.exception;

import com.example.pms.domain.exception.DomainException;
import com.example.pms.domain.exception.DuplicateItemException;
import com.example.pms.domain.exception.InsufficientInventoryException;
import com.example.pms.domain.exception.InvalidOrderStateException;
import com.example.pms.domain.exception.ItemNotFoundException;
import com.example.pms.domain.exception.PurchaseOrderNotFoundException;
import com.example.pms.domain.exception.WorkOrderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Web 画面用グローバル例外ハンドラ.
 */
@ControllerAdvice(basePackages = "com.example.pms.infrastructure.in.web")
@SuppressWarnings("PMD.GuardLogStatement")
public class WebExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebExceptionHandler.class);

    /**
     * 品目が見つからない例外をハンドリング.
     */
    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleItemNotFoundException(ItemNotFoundException ex, Model model) {
        LOGGER.warn("品目が見つかりません: {}", ex.getMessage());
        model.addAttribute("errorTitle", "品目が見つかりません");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    /**
     * 発注が見つからない例外をハンドリング.
     */
    @ExceptionHandler(PurchaseOrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePurchaseOrderNotFoundException(PurchaseOrderNotFoundException ex, Model model) {
        LOGGER.warn("発注が見つかりません: {}", ex.getMessage());
        model.addAttribute("errorTitle", "発注が見つかりません");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    /**
     * 作業指示が見つからない例外をハンドリング.
     */
    @ExceptionHandler(WorkOrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleWorkOrderNotFoundException(WorkOrderNotFoundException ex, Model model) {
        LOGGER.warn("作業指示が見つかりません: {}", ex.getMessage());
        model.addAttribute("errorTitle", "作業指示が見つかりません");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    /**
     * 品目コード重複例外をハンドリング.
     */
    @ExceptionHandler(DuplicateItemException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateItemException(DuplicateItemException ex, Model model) {
        LOGGER.warn("品目コード重複: {}", ex.getMessage());
        model.addAttribute("errorTitle", "品目コード重複");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/business-error";
    }

    /**
     * 在庫不足例外をハンドリング.
     */
    @ExceptionHandler(InsufficientInventoryException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleInsufficientInventoryException(InsufficientInventoryException ex, Model model) {
        LOGGER.warn("在庫不足: {}", ex.getMessage());
        model.addAttribute("errorTitle", "在庫不足");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/business-error";
    }

    /**
     * 発注状態不正例外をハンドリング.
     */
    @ExceptionHandler(InvalidOrderStateException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleInvalidOrderStateException(InvalidOrderStateException ex, Model model) {
        LOGGER.warn("発注状態不正: {}", ex.getMessage());
        model.addAttribute("errorTitle", "発注状態不正");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/business-error";
    }

    /**
     * ドメイン例外をハンドリング.
     */
    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDomainException(DomainException ex, Model model) {
        LOGGER.warn("ドメイン例外: {}", ex.getMessage());
        model.addAttribute("errorTitle", "業務エラー");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/business-error";
    }

    /**
     * その他の例外をハンドリング.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        LOGGER.error("予期しないエラー", ex);
        model.addAttribute("errorTitle", "システムエラー");
        model.addAttribute("errorMessage", "予期しないエラーが発生しました。管理者に連絡してください。");
        model.addAttribute("errorDetail", ex.getMessage());
        return "error/500";
    }
}
