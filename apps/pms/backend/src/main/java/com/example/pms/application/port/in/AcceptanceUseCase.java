package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.purchase.Acceptance;

import java.util.List;
import java.util.Optional;

/**
 * 検収ユースケース（Input Port）.
 */
public interface AcceptanceUseCase {

    /**
     * ページネーション付きで検収一覧を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword キーワード（null可）
     * @return ページネーション結果
     */
    PageResult<Acceptance> getAcceptanceList(int page, int size, String keyword);

    /**
     * 全検収を取得する.
     *
     * @return 検収リスト
     */
    List<Acceptance> getAllAcceptances();

    /**
     * 検収を取得する.
     *
     * @param acceptanceNumber 検収番号
     * @return 検収
     */
    Optional<Acceptance> getAcceptance(String acceptanceNumber);

    /**
     * 検収を登録する.
     *
     * @param acceptance 検収
     * @return 登録した検収
     */
    Acceptance createAcceptance(Acceptance acceptance);

    /**
     * 検収を更新する.
     *
     * @param acceptanceNumber 検収番号
     * @param acceptance 検収
     * @return 更新した検収
     */
    Acceptance updateAcceptance(String acceptanceNumber, Acceptance acceptance);

    /**
     * 検収を削除する.
     *
     * @param acceptanceNumber 検収番号
     */
    void deleteAcceptance(String acceptanceNumber);
}
