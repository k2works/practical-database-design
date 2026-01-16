package com.example.pms.application.port.out;

import com.example.pms.domain.model.department.Department;

import java.util.List;
import java.util.Optional;

/**
 * 部門マスタリポジトリインターフェース.
 */
public interface DepartmentRepository {
    void save(Department department);
    Optional<Department> findByDepartmentCode(String departmentCode);
    List<Department> findAll();
    void update(Department department);
    void deleteByDepartmentCode(String departmentCode);
    void deleteAll();

    /**
     * ページネーション付きで部門を検索する.
     *
     * @param keyword 検索キーワード（部門コードまたは部門名）
     * @param limit 取得件数
     * @param offset オフセット
     * @return 部門リスト
     */
    List<Department> findWithPagination(String keyword, int limit, int offset);

    /**
     * 検索条件に一致する部門の件数を取得する.
     *
     * @param keyword 検索キーワード
     * @return 件数
     */
    long count(String keyword);
}
