package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreateLotMasterCommand;
import com.example.pms.application.port.in.command.UpdateLotMasterCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.LotMaster;

import java.util.Optional;

/**
 * ロットマスタユースケース.
 */
public interface LotMasterUseCase {

    /**
     * ロットマスタ一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード（ロット番号・品目コード）
     * @return ページング結果
     */
    PageResult<LotMaster> getLotMasterList(int page, int size, String keyword);

    /**
     * ロットマスタを取得する.
     *
     * @param lotNumber ロット番号
     * @return ロットマスタ
     */
    Optional<LotMaster> getLotMaster(String lotNumber);

    /**
     * ロットマスタを登録する.
     *
     * @param command 登録コマンド
     * @return 登録されたロットマスタ
     */
    LotMaster createLotMaster(CreateLotMasterCommand command);

    /**
     * ロットマスタを更新する.
     *
     * @param lotNumber ロット番号
     * @param command 更新コマンド
     * @return 更新されたロットマスタ
     */
    LotMaster updateLotMaster(String lotNumber, UpdateLotMasterCommand command);

    /**
     * ロットマスタを削除する.
     *
     * @param lotNumber ロット番号
     */
    void deleteLotMaster(String lotNumber);
}
