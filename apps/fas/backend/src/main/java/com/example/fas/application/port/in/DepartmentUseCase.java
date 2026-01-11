package com.example.fas.application.port.in;

import com.example.fas.application.port.in.command.CreateDepartmentCommand;
import com.example.fas.application.port.in.command.UpdateDepartmentCommand;
import com.example.fas.application.port.in.dto.DepartmentResponse;
import com.example.fas.domain.model.common.PageResult;
import java.util.List;

/**
 * 部門ユースケース（Input Port）.
 */
public interface DepartmentUseCase {

    /**
     * 部門を取得.
     *
     * @param departmentCode 部門コード
     * @return 部門レスポンス
     */
    DepartmentResponse getDepartment(String departmentCode);

    /**
     * 全部門を取得.
     *
     * @return 部門レスポンスリスト
     */
    List<DepartmentResponse> getAllDepartments();

    /**
     * ページネーション付きで部門を取得.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param keyword キーワード
     * @param level   組織階層（null の場合は全階層）
     * @return ページネーション結果
     */
    PageResult<DepartmentResponse> getDepartments(int page, int size, String keyword, Integer level);

    /**
     * 最下層部門を取得.
     *
     * @return 部門レスポンスリスト
     */
    List<DepartmentResponse> getLowestLevelDepartments();

    /**
     * 部門を登録.
     *
     * @param command 登録コマンド
     * @return 部門レスポンス
     */
    DepartmentResponse createDepartment(CreateDepartmentCommand command);

    /**
     * 部門を更新.
     *
     * @param departmentCode 部門コード
     * @param command        更新コマンド
     * @return 部門レスポンス
     */
    DepartmentResponse updateDepartment(String departmentCode, UpdateDepartmentCommand command);

    /**
     * 部門を削除.
     *
     * @param departmentCode 部門コード
     */
    void deleteDepartment(String departmentCode);
}
