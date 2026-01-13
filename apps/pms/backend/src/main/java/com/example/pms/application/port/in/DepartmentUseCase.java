package com.example.pms.application.port.in;

import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.department.Department;

import java.util.List;
import java.util.Optional;

/**
 * 部門ユースケースインターフェース（Input Port）.
 */
public interface DepartmentUseCase {

    /**
     * 部門一覧をページネーション付きで取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード（部門コードまたは部門名）
     * @return ページ結果
     */
    PageResult<Department> getDepartments(int page, int size, String keyword);

    /**
     * すべての部門を取得する.
     *
     * @return 部門リスト
     */
    List<Department> getAllDepartments();

    /**
     * 部門を登録する.
     *
     * @param department 部門
     * @return 登録した部門
     */
    Department createDepartment(Department department);

    /**
     * 部門を取得する.
     *
     * @param departmentCode 部門コード
     * @return 部門
     */
    Optional<Department> getDepartment(String departmentCode);

    /**
     * 部門を更新する.
     *
     * @param departmentCode 部門コード
     * @param department 部門
     * @return 更新した部門
     */
    Department updateDepartment(String departmentCode, Department department);

    /**
     * 部門を削除する.
     *
     * @param departmentCode 部門コード
     */
    void deleteDepartment(String departmentCode);
}
