package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.purchase.Receiving;
import com.example.pms.domain.model.purchase.ReceivingType;

import java.util.List;
import java.util.Optional;

/**
 * 入荷受入ユースケース（Input Port）.
 */
public interface ReceivingUseCase {

    /**
     * ページネーション付きで入荷受入一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param receivingType 入荷種別（null可）
     * @param keyword キーワード（null可）
     * @return ページネーション結果
     */
    PageResult<Receiving> getReceivingList(int page, int size, ReceivingType receivingType, String keyword);

    /**
     * 全入荷受入を取得する.
     *
     * @return 入荷受入リスト
     */
    List<Receiving> getAllReceivings();

    /**
     * 入荷受入を取得する.
     *
     * @param receivingNumber 入荷番号
     * @return 入荷受入
     */
    Optional<Receiving> getReceiving(String receivingNumber);

    /**
     * 入荷受入を取得する（検査を含む）.
     *
     * @param receivingNumber 入荷番号
     * @return 入荷受入
     */
    Optional<Receiving> getReceivingWithInspections(String receivingNumber);

    /**
     * 入荷受入を登録する.
     *
     * @param receiving 入荷受入
     * @return 登録した入荷受入
     */
    Receiving createReceiving(Receiving receiving);

    /**
     * 入荷受入を更新する.
     *
     * @param receivingNumber 入荷番号
     * @param receiving 入荷受入
     * @return 更新した入荷受入
     */
    Receiving updateReceiving(String receivingNumber, Receiving receiving);

    /**
     * 入荷受入を削除する.
     *
     * @param receivingNumber 入荷番号
     */
    void deleteReceiving(String receivingNumber);
}
