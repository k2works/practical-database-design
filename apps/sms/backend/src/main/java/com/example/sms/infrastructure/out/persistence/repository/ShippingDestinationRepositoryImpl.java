package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.ShippingDestinationRepository;
import com.example.sms.domain.model.partner.ShippingDestination;
import com.example.sms.infrastructure.out.persistence.mapper.ShippingDestinationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 出荷先リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class ShippingDestinationRepositoryImpl implements ShippingDestinationRepository {

    private final ShippingDestinationMapper shippingDestinationMapper;

    @Override
    public void save(ShippingDestination shippingDestination) {
        shippingDestinationMapper.insert(shippingDestination);
    }

    @Override
    public Optional<ShippingDestination> findByKey(String partnerCode, String customerBranchNumber,
                                                   String shippingNumber) {
        return shippingDestinationMapper.findByKey(partnerCode, customerBranchNumber, shippingNumber);
    }

    @Override
    public List<ShippingDestination> findByCustomer(String partnerCode, String customerBranchNumber) {
        return shippingDestinationMapper.findByCustomer(partnerCode, customerBranchNumber);
    }

    @Override
    public List<ShippingDestination> findAll() {
        return shippingDestinationMapper.findAll();
    }

    @Override
    public void update(ShippingDestination shippingDestination) {
        shippingDestinationMapper.update(shippingDestination);
    }

    @Override
    public void deleteByKey(String partnerCode, String customerBranchNumber, String shippingNumber) {
        shippingDestinationMapper.deleteByKey(partnerCode, customerBranchNumber, shippingNumber);
    }

    @Override
    public void deleteByCustomer(String partnerCode, String customerBranchNumber) {
        shippingDestinationMapper.deleteByCustomer(partnerCode, customerBranchNumber);
    }

    @Override
    public void deleteAll() {
        shippingDestinationMapper.deleteAll();
    }
}
