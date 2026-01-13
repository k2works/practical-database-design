package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.staff.Staff;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 担当者ユースケース（Input Port）.
 */
public interface StaffUseCase {

    /**
     * ページネーション付きで担当者を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword キーワード（null可）
     * @return ページネーション結果
     */
    PageResult<Staff> getStaffList(int page, int size, String keyword);

    /**
     * 担当者を取得する.
     *
     * @param staffCode 担当者コード
     * @param effectiveFrom 適用開始日
     * @return 担当者
     */
    Optional<Staff> getStaff(String staffCode, LocalDate effectiveFrom);

    /**
     * 全担当者を取得する.
     *
     * @return 担当者リスト
     */
    List<Staff> getAllStaff();

    /**
     * 担当者を登録する.
     *
     * @param staff 担当者
     * @return 登録した担当者
     */
    Staff createStaff(Staff staff);

    /**
     * 担当者を更新する.
     *
     * @param staffCode 担当者コード
     * @param effectiveFrom 適用開始日
     * @param staff 担当者
     * @return 更新した担当者
     */
    Staff updateStaff(String staffCode, LocalDate effectiveFrom, Staff staff);

    /**
     * 担当者を削除する.
     *
     * @param staffCode 担当者コード
     * @param effectiveFrom 適用開始日
     */
    void deleteStaff(String staffCode, LocalDate effectiveFrom);
}
