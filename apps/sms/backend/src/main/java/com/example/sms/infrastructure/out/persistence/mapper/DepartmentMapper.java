package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.department.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 部門マッパー.
 */
@Mapper
public interface DepartmentMapper {

    void insert(Department department);

    Optional<Department> findByCode(String departmentCode);

    Optional<Department> findByCodeAndDate(String departmentCode, LocalDate baseDate);

    List<Department> findAll();

    List<Department> findByHierarchyLevel(int level);

    List<Department> findByPathPrefix(String pathPrefix);

    /**
     * ページネーション付きで部門を検索.
     *
     * @param offset オフセット
     * @param limit  リミット
     * @param level  階層レベル
     * @param keyword キーワード
     * @return 部門リスト
     */
    List<Department> findWithPagination(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("level") Integer level,
        @Param("keyword") String keyword);

    /**
     * 部門の総件数を取得.
     *
     * @param level   階層レベル
     * @param keyword キーワード
     * @return 総件数
     */
    long count(@Param("level") Integer level, @Param("keyword") String keyword);

    void update(Department department);

    void deleteAll();
}
