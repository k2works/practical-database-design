package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.supplier.Supplier;

import java.util.List;

/**
 * 取引先ユースケースインターフェース（Input Port）.
 */
public interface SupplierUseCase {

    /**
     * 取引先一覧をページネーション付きで取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード（取引先コードまたは取引先名）
     * @return ページ結果
     */
    PageResult<Supplier> getSuppliers(int page, int size, String keyword);

    /**
     * すべての取引先を取得する.
     *
     * @return 取引先リスト
     */
    List<Supplier> getAllSuppliers();

    /**
     * 取引先を登録する.
     *
     * @param supplier 取引先
     * @return 登録した取引先
     */
    Supplier createSupplier(Supplier supplier);

    /**
     * 取引先を取得する.
     *
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     * @return 取引先
     */
    java.util.Optional<Supplier> getSupplier(String supplierCode, java.time.LocalDate effectiveFrom);

    /**
     * 取引先を更新する.
     *
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     * @param supplier 取引先
     * @return 更新した取引先
     */
    Supplier updateSupplier(String supplierCode, java.time.LocalDate effectiveFrom, Supplier supplier);

    /**
     * 取引先を削除する.
     *
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     */
    void deleteSupplier(String supplierCode, java.time.LocalDate effectiveFrom);
}
