package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreateUnitCommand;
import com.example.pms.application.port.in.command.UpdateUnitCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.unit.Unit;

import java.util.List;
import java.util.Optional;

/**
 * 単位ユースケースインターフェース（Input Port）.
 */
public interface UnitUseCase {

    /**
     * 単位一覧をページネーション付きで取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード（単位コードまたは単位名）
     * @return ページ結果
     */
    PageResult<Unit> getUnits(int page, int size, String keyword);

    /**
     * すべての単位を取得する.
     *
     * @return 単位リスト
     */
    List<Unit> getAllUnits();

    /**
     * 単位を登録する.
     *
     * @param command 登録コマンド
     * @return 登録した単位
     */
    Unit createUnit(CreateUnitCommand command);

    /**
     * 単位を取得する.
     *
     * @param unitCode 単位コード
     * @return 単位
     */
    Optional<Unit> getUnit(String unitCode);

    /**
     * 単位を更新する.
     *
     * @param unitCode 単位コード
     * @param command 更新コマンド
     * @return 更新した単位
     */
    Unit updateUnit(String unitCode, UpdateUnitCommand command);

    /**
     * 単位を削除する.
     *
     * @param unitCode 単位コード
     */
    void deleteUnit(String unitCode);
}
