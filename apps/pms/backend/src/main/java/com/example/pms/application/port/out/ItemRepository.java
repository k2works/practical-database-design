package com.example.pms.application.port.out;

import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 品目リポジトリ（Output Port）
 * ドメイン層がデータアクセスに依存しないためのインターフェース
 */
public interface ItemRepository {

    /**
     * 品目を保存する
     */
    void save(Item item);

    /**
     * 品目コードで品目を検索する（最新の適用開始日）
     */
    Optional<Item> findByItemCode(String itemCode);

    /**
     * 品目コードと基準日で品目を検索する
     */
    Optional<Item> findByItemCodeAndDate(String itemCode, LocalDate baseDate);

    /**
     * すべての品目を取得する
     */
    List<Item> findAll();

    /**
     * 品目区分で品目を検索する
     */
    List<Item> findByCategory(ItemCategory category);

    /**
     * キーワードで品目を検索する（品目コードまたは品名）
     */
    List<Item> searchByKeyword(String keyword);

    /**
     * 品目を更新する
     */
    void update(Item item);

    /**
     * 品目コードで品目を削除する
     */
    void deleteByItemCode(String itemCode);

    /**
     * すべての品目を削除する
     */
    void deleteAll();

    /**
     * ページネーション付きで品目を検索する
     *
     * @param category 品目区分（null可）
     * @param keyword 検索キーワード（null可）
     * @param limit 取得件数
     * @param offset オフセット
     * @return 品目リスト
     */
    List<Item> findWithPagination(ItemCategory category, String keyword, int limit, int offset);

    /**
     * 条件に一致する品目の件数を取得する
     *
     * @param category 品目区分（null可）
     * @param keyword 検索キーワード（null可）
     * @return 件数
     */
    long count(ItemCategory category, String keyword);
}
