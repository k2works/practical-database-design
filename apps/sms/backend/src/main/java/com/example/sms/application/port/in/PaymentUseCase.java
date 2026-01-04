package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreatePaymentCommand;
import com.example.sms.domain.model.payment.Payment;
import com.example.sms.domain.model.payment.PaymentStatus;

import java.util.List;

/**
 * 支払ユースケース（Input Port）.
 */
public interface PaymentUseCase {

    /**
     * 支払を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された支払
     */
    Payment createPayment(CreatePaymentCommand command);

    /**
     * 全支払を取得する.
     *
     * @return 支払リスト
     */
    List<Payment> getAllPayments();

    /**
     * 支払番号で支払を取得する.
     *
     * @param paymentNumber 支払番号
     * @return 支払
     */
    Payment getPaymentByNumber(String paymentNumber);

    /**
     * 支払番号で支払（明細含む）を取得する.
     *
     * @param paymentNumber 支払番号
     * @return 支払（明細含む）
     */
    Payment getPaymentWithDetails(String paymentNumber);

    /**
     * ステータスで支払を検索する.
     *
     * @param status 支払ステータス
     * @return 支払リスト
     */
    List<Payment> getPaymentsByStatus(PaymentStatus status);

    /**
     * 仕入先コードで支払を検索する.
     *
     * @param supplierCode 仕入先コード
     * @return 支払リスト
     */
    List<Payment> getPaymentsBySupplier(String supplierCode);
}
