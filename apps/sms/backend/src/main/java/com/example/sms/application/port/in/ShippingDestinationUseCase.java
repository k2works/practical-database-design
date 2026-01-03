package com.example.sms.application.port.in;

import com.example.sms.domain.model.partner.ShippingDestination;

import java.util.List;

/**
 * 出荷先ユースケース（Input Port）.
 */
public interface ShippingDestinationUseCase {

    List<ShippingDestination> getAllShippingDestinations();

    ShippingDestination getShippingDestination(String partnerCode, String branchNumber, String shippingNumber);

    List<ShippingDestination> getShippingDestinationsByCustomer(String partnerCode, String branchNumber);

    void createShippingDestination(ShippingDestination shippingDestination);

    void updateShippingDestination(ShippingDestination shippingDestination);

    void deleteShippingDestination(String partnerCode, String branchNumber, String shippingNumber);
}
