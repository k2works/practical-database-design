package com.example.sms.application.service;

import com.example.sms.application.port.in.ShippingDestinationUseCase;
import com.example.sms.application.port.out.ShippingDestinationRepository;
import com.example.sms.domain.exception.ShippingDestinationNotFoundException;
import com.example.sms.domain.model.partner.ShippingDestination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 出荷先サービス.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ShippingDestinationService implements ShippingDestinationUseCase {

    private final ShippingDestinationRepository shippingDestinationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ShippingDestination> getAllShippingDestinations() {
        return shippingDestinationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingDestination getShippingDestination(String partnerCode, String branchNumber,
                                                      String shippingNumber) {
        return shippingDestinationRepository.findByKey(partnerCode, branchNumber, shippingNumber)
                .orElseThrow(() -> new ShippingDestinationNotFoundException(partnerCode, branchNumber, shippingNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingDestination> getShippingDestinationsByCustomer(String partnerCode, String branchNumber) {
        return shippingDestinationRepository.findByCustomer(partnerCode, branchNumber);
    }

    @Override
    public void createShippingDestination(ShippingDestination shippingDestination) {
        shippingDestinationRepository.save(shippingDestination);
    }

    @Override
    public void updateShippingDestination(ShippingDestination shippingDestination) {
        shippingDestinationRepository.update(shippingDestination);
    }

    @Override
    public void deleteShippingDestination(String partnerCode, String branchNumber, String shippingNumber) {
        shippingDestinationRepository.deleteByKey(partnerCode, branchNumber, shippingNumber);
    }
}
