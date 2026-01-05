package com.example.sms.application.port.in;

import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.employee.Employee;

import java.util.List;

/**
 * 社員ユースケース（Input Port）.
 */
public interface EmployeeUseCase {

    /**
     * 全社員を取得する.
     *
     * @return 社員リスト
     */
    List<Employee> getAllEmployees();

    /**
     * ページネーション付きで社員を取得.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param departmentCode 部門コード
     * @param keyword キーワード
     * @return ページネーション結果
     */
    PageResult<Employee> getEmployees(int page, int size, String departmentCode, String keyword);

    /**
     * 社員コードで社員を取得する.
     *
     * @param employeeCode 社員コード
     * @return 社員
     */
    Employee getEmployeeByCode(String employeeCode);

    /**
     * 部門コードで社員を検索する.
     *
     * @param departmentCode 部門コード
     * @return 社員リスト
     */
    List<Employee> getEmployeesByDepartment(String departmentCode);

    /**
     * 社員を登録する.
     *
     * @param employee 社員
     */
    void createEmployee(Employee employee);

    /**
     * 社員を更新する.
     *
     * @param employee 社員
     */
    void updateEmployee(Employee employee);
}
