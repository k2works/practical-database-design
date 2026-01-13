package com.example.pms.infrastructure.out.persistence.mapper;

import com.example.pms.domain.model.plan.Order;
import com.example.pms.domain.model.plan.PlanStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Order order);
    Order findById(Integer id);
    Order findByOrderNumber(String orderNumber);
    /**
     * オーダ番号で検索（所要を含む）.
     */
    Order findByOrderNumberWithRequirements(String orderNumber);
    List<Order> findByMpsId(Integer mpsId);
    List<Order> findByParentOrderId(Integer parentOrderId);
    List<Order> findAll();

    /**
     * ページネーション付きでオーダを検索する.
     */
    List<Order> findWithPagination(
        @Param("status") PlanStatus status,
        @Param("keyword") String keyword,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    /**
     * 検索条件に合致するオーダの件数を取得する.
     */
    long count(
        @Param("status") PlanStatus status,
        @Param("keyword") String keyword
    );

    void updateStatus(@Param("id") Integer id, @Param("status") PlanStatus status);
    void updateParentOrderId(@Param("id") Integer id, @Param("parentOrderId") Integer parentOrderId);
    void deleteAll();
}
