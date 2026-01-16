package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreateSupplierCommand;
import com.example.pms.application.port.in.command.UpdateSupplierCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.supplier.Supplier;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
     * @param command 登録コマンド
     * @return 登録した取引先
     */
    Supplier createSupplier(CreateSupplierCommand command);

    /**
     * 取引先を取得する.
     *
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     * @return 取引先
     */
    Optional<Supplier> getSupplier(String supplierCode, LocalDate effectiveFrom);

    /**
     * 取引先を更新する.
     *
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     * @param command 更新コマンド
     * @return 更新した取引先
     */
    Supplier updateSupplier(String supplierCode, LocalDate effectiveFrom, UpdateSupplierCommand command);

    /**
     * 取引先を削除する.
     *
     * @param supplierCode 取引先コード
     * @param effectiveFrom 適用開始日
     */
    void deleteSupplier(String supplierCode, LocalDate effectiveFrom);
}
