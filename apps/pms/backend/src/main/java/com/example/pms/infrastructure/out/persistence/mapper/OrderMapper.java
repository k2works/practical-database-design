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
    List<Order> findByMpsId(Integer mpsId);
    List<Order> findByParentOrderId(Integer parentOrderId);
    List<Order> findAll();
    void updateStatus(@Param("id") Integer id, @Param("status") PlanStatus status);
    void updateParentOrderId(@Param("id") Integer id, @Param("parentOrderId") Integer parentOrderId);
    void deleteAll();
}
