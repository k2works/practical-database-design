package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreateProcessRouteCommand;
import com.example.pms.application.port.in.command.UpdateProcessRouteCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.ProcessRoute;

import java.util.List;
import java.util.Optional;

/**
 * 工程表ユースケース（Input Port）.
 */
public interface ProcessRouteUseCase {

    /**
     * ページネーション付きで工程表を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param itemCode 品目コード（null可）
     * @return ページネーション結果
     */
    PageResult<ProcessRoute> getProcessRoutes(int page, int size, String itemCode);

    /**
     * 品目コードで工程表を取得する.
     *
     * @param itemCode 品目コード
     * @return 工程表リスト
     */
    List<ProcessRoute> getProcessRoutesByItemCode(String itemCode);

    /**
     * 工程表を登録する.
     *
     * @param command 登録コマンド
     * @return 登録した工程表
     */
    ProcessRoute createProcessRoute(CreateProcessRouteCommand command);

    /**
     * 工程表を取得する.
     *
     * @param itemCode 品目コード
     * @param sequence 工順
     * @return 工程表
     */
    Optional<ProcessRoute> getProcessRoute(String itemCode, Integer sequence);

    /**
     * 工程表を更新する.
     *
     * @param itemCode 品目コード
     * @param sequence 工順
     * @param command 更新コマンド
     * @return 更新した工程表
     */
    ProcessRoute updateProcessRoute(String itemCode, Integer sequence, UpdateProcessRouteCommand command);

    /**
     * 工程表を削除する.
     *
     * @param itemCode 品目コード
     * @param sequence 工順
     */
    void deleteProcessRoute(String itemCode, Integer sequence);
}
