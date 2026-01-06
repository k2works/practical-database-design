package com.example.fas.infrastructure.out.persistence.mapper;

import com.example.fas.domain.model.department.Department;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 部門マッパー.
 */
@Mapper
public interface DepartmentMapper {

    /**
     * 部門を登録.
     *
     * @param department 部門
     */
    void insert(Department department);

    /**
     * 部門コードで検索.
     *
     * @param departmentCode 部門コード
     * @return 部門
     */
    Optional<Department> findByCode(@Param("departmentCode") String departmentCode);

    /**
     * 全部門を取得.
     *
     * @return 全部門のリスト
     */
    List<Department> findAll();

    /**
     * 階層で検索.
     *
     * @param level 組織階層
     * @return 該当階層の部門リスト
     */
    List<Department> findByOrganizationLevel(@Param("level") int level);

    /**
     * 最下層部門のみ取得.
     *
     * @return 最下層部門のリスト
     */
    List<Department> findLowestLevel();

    /**
     * パス接頭辞で検索.
     *
     * @param pathPrefix パスの接頭辞
     * @return 配下部門のリスト
     */
    List<Department> findByPathPrefix(@Param("pathPrefix") String pathPrefix);

    /**
     * 部門を削除.
     *
     * @param departmentCode 部門コード
     */
    void deleteByCode(@Param("departmentCode") String departmentCode);

    /**
     * 全件削除.
     */
    void deleteAll();
}
