package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreatePurchaseCommand;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.purchase.Purchase;

import java.util.List;

/**
 * 仕入ユースケース（Input Port）.
 */
public interface PurchaseUseCase {

    /**
     * 仕入を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された仕入
     */
    Purchase createPurchase(CreatePurchaseCommand command);

    /**
     * 全仕入を取得する.
     *
     * @return 仕入リスト
     */
    List<Purchase> getAllPurchases();

    /**
     * ページネーション付きで仕入を取得.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード
     * @return ページ結果
     */
    PageResult<Purchase> getPurchases(int page, int size, String keyword);

    /**
     * 仕入番号で仕入を取得する.
     *
     * @param purchaseNumber 仕入番号
     * @return 仕入
     */
    Purchase getPurchaseByNumber(String purchaseNumber);

    /**
     * 仕入番号で仕入（明細含む）を取得する.
     *
     * @param purchaseNumber 仕入番号
     * @return 仕入（明細含む）
     */
    Purchase getPurchaseWithDetails(String purchaseNumber);

    /**
     * 仕入先コードで仕入を検索する.
     *
     * @param supplierCode 仕入先コード
     * @return 仕入リスト
     */
    List<Purchase> getPurchasesBySupplier(String supplierCode);

    /**
     * 入荷IDで仕入を検索する.
     *
     * @param receivingId 入荷ID
     * @return 仕入リスト
     */
    List<Purchase> getPurchasesByReceiving(Integer receivingId);
}
