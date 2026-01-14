package com.example.pms.application.port.in;

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
     * @param lotMaster ロットマスタ
     * @return 登録されたロットマスタ
     */
    LotMaster createLotMaster(LotMaster lotMaster);

    /**
     * ロットマスタを更新する.
     *
     * @param lotNumber ロット番号
     * @param lotMaster 更新データ
     * @return 更新されたロットマスタ
     */
    LotMaster updateLotMaster(String lotNumber, LotMaster lotMaster);

    /**
     * ロットマスタを削除する.
     *
     * @param lotNumber ロット番号
     */
    void deleteLotMaster(String lotNumber);
}
