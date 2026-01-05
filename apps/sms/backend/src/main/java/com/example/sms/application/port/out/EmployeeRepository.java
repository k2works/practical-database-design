package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.employee.Employee;

import java.util.List;
import java.util.Optional;

/**
 * 社員リポジトリ（Output Port）.
 */
public interface EmployeeRepository {

    void save(Employee employee);

    Optional<Employee> findByCode(String employeeCode);

    List<Employee> findAll();

    List<Employee> findByDepartmentCode(String departmentCode);

    /**
     * ページネーション付きで社員を検索.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param departmentCode 部門コード
     * @param keyword キーワード
     * @return ページネーション結果
     */
    PageResult<Employee> findWithPagination(int page, int size, String departmentCode, String keyword);

    void update(Employee employee);

    void deleteAll();
}
