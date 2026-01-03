package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.domain.exception.BusinessRuleViolationException;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Web 層のグローバル例外ハンドラー.
 * Thymeleaf ビューでエラーページを表示する。
 */
@ControllerAdvice(basePackages = "com.example.sms.infrastructure.in.web")
@SuppressWarnings("PMD.GuardLogStatement")
public class WebExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebExceptionHandler.class);

    /**
     * リソース未検出例外を処理.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException e, Model model) {
        LOG.warn("リソース未検出: {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "error/404";
    }

    /**
     * ビジネスルール違反例外を処理.
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public String handleBusinessRuleViolation(BusinessRuleViolationException e, Model model) {
        LOG.warn("業務ルール違反: {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("errorCode", "BUSINESS_ERROR");
        return "error/business";
    }

    /**
     * 楽観ロック例外を処理.
     */
    @ExceptionHandler(OptimisticLockException.class)
    public String handleOptimisticLock(OptimisticLockException e, Model model) {
        LOG.warn("楽観ロックエラー: {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("errorCode", "OPTIMISTIC_LOCK");
        return "error/business";
    }

    /**
     * データ整合性違反例外を処理.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException e, Model model) {
        LOG.warn("データ整合性エラー: {}", e.getMessage());
        String userMessage = parseDataIntegrityErrorMessage(e);
        model.addAttribute("errorMessage", userMessage);
        model.addAttribute("errorCode", "DATA_INTEGRITY_ERROR");
        return "error/business";
    }

    /**
     * データ整合性エラーメッセージを解析してユーザーフレンドリーなメッセージに変換.
     */
    private String parseDataIntegrityErrorMessage(DataIntegrityViolationException e) {
        String message = e.getMessage();
        if (message == null) {
            return "データの整合性エラーが発生しました。入力内容を確認してください。";
        }

        if (message.contains("fk_受注データ_顧客")) {
            return "指定された顧客が存在しません。顧客マスタを確認してください。";
        }
        if (message.contains("fk_受注データ_商品") || message.contains("商品コード")) {
            return "指定された商品が存在しません。商品マスタを確認してください。";
        }
        if (message.contains("fk_") && message.contains("顧客")) {
            return "指定された顧客が存在しません。顧客マスタを確認してください。";
        }
        if (message.contains("fk_") && message.contains("取引先")) {
            return "指定された取引先が存在しません。取引先マスタを確認してください。";
        }
        if (message.contains("duplicate key") || message.contains("重複")) {
            return "既に同じデータが登録されています。入力内容を確認してください。";
        }

        return "データの整合性エラーが発生しました。入力内容を確認してください。";
    }

    /**
     * 一般例外を処理.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception e, Model model) {
        LOG.error("システムエラー", e);
        model.addAttribute("errorMessage", "システムエラーが発生しました。管理者にお問い合わせください。");
        return "error/500";
    }
}
