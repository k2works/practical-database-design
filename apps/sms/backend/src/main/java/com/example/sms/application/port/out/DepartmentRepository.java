package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.department.Department;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 部門リポジトリ（Output Port）.
 */
public interface DepartmentRepository {

    void save(Department department);

    Optional<Department> findByCode(String departmentCode);

    Optional<Department> findByCodeAndDate(String departmentCode, LocalDate baseDate);

    List<Department> findAll();

    List<Department> findByHierarchyLevel(int level);

    List<Department> findChildren(String parentPath);

    /**
     * ページネーション付きで部門を検索.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param level   階層レベル
     * @param keyword キーワード
     * @return ページネーション結果
     */
    PageResult<Department> findWithPagination(int page, int size, Integer level, String keyword);

    void update(Department department);

    void deleteAll();
}
