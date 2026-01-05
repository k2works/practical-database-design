package com.example.sms.application.port.out;

import com.example.sms.domain.model.partner.ShippingDestination;

import java.util.List;
import java.util.Optional;

/**
 * 出荷先リポジトリ（Output Port）.
 */
public interface ShippingDestinationRepository {

    void save(ShippingDestination shippingDestination);

    Optional<ShippingDestination> findByKey(String partnerCode, String customerBranchNumber, String shippingNumber);

    List<ShippingDestination> findByCustomer(String partnerCode, String customerBranchNumber);

    List<ShippingDestination> findAll();

    void update(ShippingDestination shippingDestination);

    void deleteByKey(String partnerCode, String customerBranchNumber, String shippingNumber);

    void deleteByCustomer(String partnerCode, String customerBranchNumber);

    void deleteAll();
}
