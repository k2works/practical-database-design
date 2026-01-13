package com.example.pms.application.port.out;

import com.example.pms.domain.model.staff.Staff;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 担当者マスタリポジトリインターフェース.
 */
public interface StaffRepository {
    void save(Staff staff);
    Optional<Staff> findByStaffCode(String staffCode);
    Optional<Staff> findByStaffCodeAndDate(String staffCode, LocalDate baseDate);
    List<Staff> findByDepartmentCode(String departmentCode);
    List<Staff> findAll();
    void update(Staff staff);
    void deleteAll();

    /**
     * ページネーション付きで担当者を取得.
     *
     * @param keyword キーワード（null可）
     * @param limit 取得件数
     * @param offset オフセット
     * @return 担当者リスト
     */
    List<Staff> findWithPagination(String keyword, int limit, int offset);

    /**
     * 条件に一致する担当者の件数を取得.
     *
     * @param keyword キーワード（null可）
     * @return 件数
     */
    long count(String keyword);

    /**
     * 担当者コードと適用開始日で担当者を取得.
     *
     * @param staffCode 担当者コード
     * @param effectiveFrom 適用開始日
     * @return 担当者
     */
    Optional<Staff> findByStaffCodeAndEffectiveFrom(String staffCode, LocalDate effectiveFrom);

    /**
     * 担当者コードと適用開始日で担当者を削除.
     *
     * @param staffCode 担当者コード
     * @param effectiveFrom 適用開始日
     */
    void deleteByStaffCodeAndEffectiveFrom(String staffCode, LocalDate effectiveFrom);
}
