package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.domain.exception.BusinessRuleViolationException;
import com.example.sms.domain.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
     * 一般例外を処理.
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, Model model) {
        LOG.error("システムエラー", e);
        model.addAttribute("errorMessage", "システムエラーが発生しました。管理者にお問い合わせください。");
        return "error/500";
    }
}
