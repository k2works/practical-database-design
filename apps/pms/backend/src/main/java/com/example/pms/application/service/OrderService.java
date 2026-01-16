package com.example.pms.application.service;

import com.example.pms.application.port.in.OrderUseCase;
import com.example.pms.application.port.out.OrderRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.plan.Order;
import com.example.pms.domain.model.plan.PlanStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * オーダ情報サービス.
 */
@Service
@Transactional(readOnly = true)
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public PageResult<Order> getOrders(int page, int size, PlanStatus status, String keyword) {
        int offset = page * size;
        List<Order> orders = orderRepository.findWithPagination(status, keyword, size, offset);
        long totalElements = orderRepository.count(status, keyword);
        return new PageResult<>(orders, page, size, totalElements);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrder(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public Optional<Order> getOrderWithRequirements(String orderNumber) {
        return orderRepository.findByOrderNumberWithRequirements(orderNumber);
    }
}
