package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreateItemCommand;
import com.example.pms.application.port.in.command.UpdateItemCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.item.Item;
import com.example.pms.domain.model.item.ItemCategory;

import java.util.List;

/**
 * 品目ユースケース（Input Port）.
 */
public interface ItemUseCase {

    /**
     * 全品目を取得する.
     *
     * @return 品目リスト
     */
    List<Item> getAllItems();

    /**
     * 品目区分で品目を取得する.
     *
     * @param category 品目区分
     * @return 品目リスト
     */
    List<Item> getItemsByCategory(ItemCategory category);

    /**
     * キーワードで品目を検索する.
     *
     * @param keyword 検索キーワード
     * @return 品目リスト
     */
    List<Item> searchItems(String keyword);

    /**
     * 品目コードで品目を取得する.
     *
     * @param itemCode 品目コード
     * @return 品目
     */
    Item getItem(String itemCode);

    /**
     * 品目を登録する.
     *
     * @param command 登録コマンド
     * @return 登録した品目
     */
    Item createItem(CreateItemCommand command);

    /**
     * 品目を更新する.
     *
     * @param itemCode 品目コード
     * @param command 更新コマンド
     * @return 更新した品目
     */
    Item updateItem(String itemCode, UpdateItemCommand command);

    /**
     * 品目を削除する.
     *
     * @param itemCode 品目コード
     */
    void deleteItem(String itemCode);

    /**
     * ページネーション付きで品目を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param category 品目区分（null可）
     * @param keyword 検索キーワード（null可）
     * @return ページネーション結果
     */
    PageResult<Item> getItems(int page, int size, ItemCategory category, String keyword);
}
