package com.example.sms.application.port.in;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.department.Department;

import java.time.LocalDate;
import java.util.List;

/**
 * 部門ユースケース（Input Port）.
 */
public interface DepartmentUseCase {

    /**
     * 全部門を取得する.
     *
     * @return 部門リスト
     */
    List<Department> getAllDepartments();

    /**
     * ページネーション付きで部門を取得.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param level   階層レベル
     * @param keyword キーワード
     * @return ページネーション結果
     */
    PageResult<Department> getDepartments(int page, int size, Integer level, String keyword);

    /**
     * 部門コードで部門を取得する.
     *
     * @param departmentCode 部門コード
     * @return 部門
     */
    Department getDepartmentByCode(String departmentCode);

    /**
     * 部門コードと基準日で部門を取得する.
     *
     * @param departmentCode 部門コード
     * @param baseDate 基準日
     * @return 部門
     */
    Department getDepartmentByCodeAndDate(String departmentCode, LocalDate baseDate);

    /**
     * 階層レベルで部門を検索する.
     *
     * @param level 階層レベル
     * @return 部門リスト
     */
    List<Department> getDepartmentsByHierarchyLevel(int level);

    /**
     * 子部門を検索する.
     *
     * @param parentPath 親パス
     * @return 部門リスト
     */
    List<Department> getChildDepartments(String parentPath);

    /**
     * 部門を登録する.
     *
     * @param department 部門
     */
    void createDepartment(Department department);

    /**
     * 部門を更新する.
     *
     * @param department 部門
     */
    void updateDepartment(Department department);
}
