package com.example.fas.application.port.out;

import com.example.fas.domain.model.common.PageResult;
import com.example.fas.domain.model.tax.TaxTransaction;
import java.util.List;
import java.util.Optional;

/**
 * 課税取引リポジトリ（Output Port）.
 */
public interface TaxTransactionRepository {

    void save(TaxTransaction taxTransaction);

    Optional<TaxTransaction> findByCode(String taxCode);

    List<TaxTransaction> findAll();

    /**
     * ページネーション付きで課税取引を検索.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param keyword キーワード（課税取引コード、課税取引名）
     * @return ページネーション結果
     */
    PageResult<TaxTransaction> findWithPagination(int page, int size, String keyword);

    void update(TaxTransaction taxTransaction);

    /**
     * 課税取引を削除.
     *
     * @param taxCode 課税取引コード
     */
    void delete(String taxCode);

    void deleteAll();
}
