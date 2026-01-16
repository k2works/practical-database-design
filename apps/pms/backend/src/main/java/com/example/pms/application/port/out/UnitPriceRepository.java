package com.example.pms.application.port.out;

import com.example.pms.domain.model.unitprice.UnitPrice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 単価マスタリポジトリインターフェース.
 */
public interface UnitPriceRepository {
    void save(UnitPrice unitPrice);
    Optional<UnitPrice> findByItemCodeAndSupplierCode(String itemCode, String supplierCode);
    Optional<UnitPrice> findByItemCodeAndSupplierCodeAndDate(String itemCode, String supplierCode, LocalDate baseDate);
    List<UnitPrice> findByItemCode(String itemCode);
    List<UnitPrice> findAll();
    void update(UnitPrice unitPrice);
    void deleteAll();

    /**
     * ページネーション付きで単価を検索する.
     *
     * @param itemCode 品目コード（検索条件）
     * @param limit 取得件数
     * @param offset オフセット
     * @return 単価リスト
     */
    List<UnitPrice> findWithPagination(String itemCode, int limit, int offset);

    /**
     * 検索条件に一致する単価の件数を取得する.
     *
     * @param itemCode 品目コード
     * @return 件数
     */
    long count(String itemCode);

    /**
     * 複合キーで単価を検索する.
     *
     * @param itemCode 品目コード
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     * @return 単価
     */
    Optional<UnitPrice> findByKey(String itemCode, String supplierCode, LocalDate effectiveFrom);

    /**
     * 単価を削除する.
     *
     * @param itemCode 品目コード
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     */
    void deleteByKey(String itemCode, String supplierCode, LocalDate effectiveFrom);
}
