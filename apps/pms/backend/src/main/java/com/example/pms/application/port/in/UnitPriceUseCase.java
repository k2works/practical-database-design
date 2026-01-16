package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreateUnitPriceCommand;
import com.example.pms.application.port.in.command.UpdateUnitPriceCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.unitprice.UnitPrice;

import java.util.List;

/**
 * 単価ユースケースインターフェース（Input Port）.
 */
public interface UnitPriceUseCase {

    /**
     * 単価一覧をページネーション付きで取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param itemCode 品目コード（検索条件）
     * @return ページ結果
     */
    PageResult<UnitPrice> getUnitPrices(int page, int size, String itemCode);

    /**
     * すべての単価を取得する.
     *
     * @return 単価リスト
     */
    List<UnitPrice> getAllUnitPrices();

    /**
     * 単価を登録する.
     *
     * @param command 登録コマンド
     * @return 登録した単価
     */
    UnitPrice createUnitPrice(CreateUnitPriceCommand command);

    /**
     * 単価を取得する.
     *
     * @param itemCode 品目コード
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     * @return 単価
     */
    java.util.Optional<UnitPrice> getUnitPrice(String itemCode, String supplierCode, java.time.LocalDate effectiveFrom);

    /**
     * 単価を更新する.
     *
     * @param itemCode 品目コード
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     * @param command 更新コマンド
     * @return 更新した単価
     */
    UnitPrice updateUnitPrice(String itemCode, String supplierCode, java.time.LocalDate effectiveFrom, UpdateUnitPriceCommand command);

    /**
     * 単価を削除する.
     *
     * @param itemCode 品目コード
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     */
    void deleteUnitPrice(String itemCode, String supplierCode, java.time.LocalDate effectiveFrom);
}
