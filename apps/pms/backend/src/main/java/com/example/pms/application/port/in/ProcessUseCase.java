package com.example.pms.application.port.in;

import com.example.pms.application.port.in.command.CreateProcessCommand;
import com.example.pms.application.port.in.command.UpdateProcessCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.Process;

import java.util.List;

/**
 * 工程ユースケース（Input Port）.
 */
public interface ProcessUseCase {

    /**
     * ページネーション付きで工程を取得する.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード（null可）
     * @return ページネーション結果
     */
    PageResult<Process> getProcesses(int page, int size, String keyword);

    /**
     * 工程コードで工程を取得する.
     *
     * @param processCode 工程コード
     * @return 工程
     */
    Process getProcess(String processCode);

    /**
     * 全工程を取得する.
     *
     * @return 工程リスト
     */
    List<Process> getAllProcesses();

    /**
     * 工程を登録する.
     *
     * @param command 登録コマンド
     * @return 登録した工程
     */
    Process createProcess(CreateProcessCommand command);

    /**
     * 工程を更新する.
     *
     * @param processCode 工程コード
     * @param command 更新コマンド
     * @return 更新した工程
     */
    Process updateProcess(String processCode, UpdateProcessCommand command);

    /**
     * 工程を削除する.
     *
     * @param processCode 工程コード
     */
    void deleteProcess(String processCode);
}
