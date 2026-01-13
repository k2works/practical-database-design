package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreateItemCommand;
import com.example.pms.application.port.in.command.UpdateItemCommand;
import com.example.pms.domain.model.item.Item;

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
}
