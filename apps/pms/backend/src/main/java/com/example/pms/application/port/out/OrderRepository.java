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
