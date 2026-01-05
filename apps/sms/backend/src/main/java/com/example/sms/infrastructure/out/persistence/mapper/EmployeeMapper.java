package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.employee.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 社員マッパー.
 */
@Mapper
public interface EmployeeMapper {

    void insert(Employee employee);

    Optional<Employee> findByCode(String employeeCode);

    List<Employee> findAll();

    List<Employee> findByDepartmentCode(String departmentCode);

    /**
     * ページネーション付きで社員を検索.
     *
     * @param offset オフセット
     * @param limit  リミット
     * @param departmentCode 部門コード
     * @param keyword キーワード
     * @return 社員リスト
     */
    List<Employee> findWithPagination(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("departmentCode") String departmentCode,
        @Param("keyword") String keyword);

    /**
     * 社員の総件数を取得.
     *
     * @param departmentCode 部門コード
     * @param keyword キーワード
     * @return 総件数
     */
    long count(@Param("departmentCode") String departmentCode, @Param("keyword") String keyword);

    void update(Employee employee);

    void deleteAll();
}
