package com.example.pms.application.port.out;

import com.example.pms.domain.model.supplier.Supplier;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 取引先マスタリポジトリインターフェース.
 */
public interface SupplierRepository {

    void save(Supplier supplier);

    Optional<Supplier> findBySupplierCode(String supplierCode);

    Optional<Supplier> findBySupplierCodeAndDate(String supplierCode, LocalDate baseDate);

    List<Supplier> findAll();

    void update(Supplier supplier);

    void deleteAll();

    /**
     * ページネーション付きで取引先を検索する.
     *
     * @param keyword 検索キーワード（取引先コードまたは取引先名）
     * @param limit 取得件数
     * @param offset オフセット
     * @return 取引先リスト
     */
    List<Supplier> findWithPagination(String keyword, int limit, int offset);

    /**
     * 検索条件に一致する取引先の件数を取得する.
     *
     * @param keyword 検索キーワード
     * @return 件数
     */
    long count(String keyword);

    /**
     * 取引先コードと適用開始日で取引先を検索する.
     *
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     * @return 取引先
     */
    Optional<Supplier> findBySupplierCodeAndEffectiveFrom(String supplierCode, LocalDate effectiveFrom);

    /**
     * 取引先を削除する.
     *
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     */
    void deleteBySupplierCodeAndEffectiveFrom(String supplierCode, LocalDate effectiveFrom);
}
