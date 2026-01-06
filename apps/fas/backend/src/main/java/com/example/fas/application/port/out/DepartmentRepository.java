package com.example.fas.application.port.out;

import com.example.fas.domain.model.department.Department;
import java.util.List;
import java.util.Optional;

/**
 * 部門リポジトリ（Output Port）.
 */
public interface DepartmentRepository {

    /**
     * 部門コードで検索.
     *
     * @param departmentCode 部門コード
     * @return 部門
     */
    Optional<Department> findByCode(String departmentCode);

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
    List<Department> findByOrganizationLevel(int level);

    /**
     * 最下層部門のみ取得.
     *
     * @return 最下層部門のリスト
     */
    List<Department> findLowestLevel();

    /**
     * 上位部門配下の部門を取得（パス検索）.
     *
     * @param pathPrefix パスの接頭辞
     * @return 配下部門のリスト
     */
    List<Department> findByPathPrefix(String pathPrefix);

    /**
     * 部門を保存.
     *
     * @param department 部門
     */
    void save(Department department);

    /**
     * 部門を削除.
     *
     * @param departmentCode 部門コード
     */
    void deleteByCode(String departmentCode);

    /**
     * 全件削除（テスト用）.
     */
    void deleteAll();
}
