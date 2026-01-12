package com.example.pms.infrastructure.out.persistence.repository;

import com.example.pms.application.port.out.OrderRepository;
import com.example.pms.domain.model.plan.Order;
import com.example.pms.domain.model.plan.PlanStatus;
import com.example.pms.infrastructure.out.persistence.mapper.OrderMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * オーダ情報リポジトリ実装
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;

    public OrderRepositoryImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public void save(Order order) {
        orderMapper.insert(order);
    }

    @Override
    public Optional<Order> findById(Integer id) {
        return Optional.ofNullable(orderMapper.findById(id));
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return Optional.ofNullable(orderMapper.findByOrderNumber(orderNumber));
    }

    @Override
    public Optional<Order> findByOrderNumberWithRequirements(String orderNumber) {
        return Optional.ofNullable(orderMapper.findByOrderNumberWithRequirements(orderNumber));
    }

    @Override
    public List<Order> findByMpsId(Integer mpsId) {
        return orderMapper.findByMpsId(mpsId);
    }

    @Override
    public List<Order> findByParentOrderId(Integer parentOrderId) {
        return orderMapper.findByParentOrderId(parentOrderId);
    }

    @Override
    public List<Order> findAll() {
        return orderMapper.findAll();
    }

    @Override
    public void updateStatus(Integer id, PlanStatus status) {
        orderMapper.updateStatus(id, status);
    }

    @Override
    public void updateParentOrderId(Integer id, Integer parentOrderId) {
        orderMapper.updateParentOrderId(id, parentOrderId);
    }

    @Override
    public void deleteAll() {
        orderMapper.deleteAll();
    }
}
