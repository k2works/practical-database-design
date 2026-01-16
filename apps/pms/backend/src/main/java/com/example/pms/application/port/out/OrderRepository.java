package com.example.pms.application.port.out;

import com.example.pms.domain.model.plan.Order;
import com.example.pms.domain.model.plan.PlanStatus;

import java.util.List;
import java.util.Optional;

/**
 * オーダ情報リポジトリ（Output Port）
 * ドメイン層がデータアクセスに依存しないためのインターフェース
 */
public interface OrderRepository {

    /**
     * オーダを保存する
     */
    void save(Order order);

    /**
     * IDでオーダを検索する
     */
    Optional<Order> findById(Integer id);

    /**
     * オーダ番号でオーダを検索する
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * オーダ番号でオーダを検索する（所要を含む）
     *
     * @param orderNumber オーダ番号
     * @return 所要を含むオーダ
     */
    Optional<Order> findByOrderNumberWithRequirements(String orderNumber);

    /**
     * MPS IDでオーダを検索する
     */
    List<Order> findByMpsId(Integer mpsId);

    /**
     * 親オーダIDで子オーダを検索する
     */
    List<Order> findByParentOrderId(Integer parentOrderId);

    /**
     * すべてのオーダを取得する
     */
    List<Order> findAll();

    /**
     * ページネーション付きでオーダを検索する
     *
     * @param status ステータス（オプション）
     * @param keyword キーワード（オーダNOまたは品目コード）
     * @param limit 取得件数
     * @param offset オフセット
     * @return オーダのリスト
     */
    List<Order> findWithPagination(PlanStatus status, String keyword, int limit, int offset);

    /**
     * 検索条件に合致するオーダの件数を取得する
     *
     * @param status ステータス（オプション）
     * @param keyword キーワード（オーダNOまたは品目コード）
     * @return 件数
     */
    long count(PlanStatus status, String keyword);

    /**
     * ステータスを更新する
     */
    void updateStatus(Integer id, PlanStatus status);

    /**
     * 親オーダIDを更新する
     */
    void updateParentOrderId(Integer id, Integer parentOrderId);

    /**
     * すべてのオーダを削除する
     */
    void deleteAll();
}
