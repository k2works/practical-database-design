package com.example.sms.application.service;

import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.command.CreatePartnerCommand;
import com.example.sms.application.port.in.command.UpdatePartnerCommand;
import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.domain.exception.DuplicatePartnerException;
import com.example.sms.domain.exception.PartnerNotFoundException;
import com.example.sms.domain.model.partner.Partner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 取引先アプリケーションサービス.
 */
@Service
@Transactional
public class PartnerService implements PartnerUseCase {

    private final PartnerRepository partnerRepository;

    public PartnerService(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    public Partner createPartner(CreatePartnerCommand command) {
        partnerRepository.findByCode(command.partnerCode())
            .ifPresent(existing -> {
                throw new DuplicatePartnerException(command.partnerCode());
            });

        Partner partner = Partner.builder()
            .partnerCode(command.partnerCode())
            .partnerName(command.partnerName())
            .partnerNameKana(command.partnerNameKana())
            .isCustomer(command.isCustomer())
            .isSupplier(command.isSupplier())
            .postalCode(command.postalCode())
            .address1(command.address1())
            .address2(command.address2())
            .classificationCode(command.classificationCode())
            .isTradingProhibited(command.isTradingProhibited())
            .isMiscellaneous(command.isMiscellaneous())
            .groupCode(command.groupCode())
            .creditLimit(command.creditLimit())
            .temporaryCreditIncrease(command.temporaryCreditIncrease())
            .build();

        partnerRepository.save(partner);
        return partner;
    }

    @Override
    public Partner updatePartner(String partnerCode, UpdatePartnerCommand command) {
        Partner existing = partnerRepository.findByCode(partnerCode)
            .orElseThrow(() -> new PartnerNotFoundException(partnerCode));

        Partner updated = Partner.builder()
            .partnerCode(partnerCode)
            .partnerName(coalesce(command.partnerName(), existing.getPartnerName()))
            .partnerNameKana(coalesce(command.partnerNameKana(), existing.getPartnerNameKana()))
            .isCustomer(coalesce(command.isCustomer(), existing.isCustomer()))
            .isSupplier(coalesce(command.isSupplier(), existing.isSupplier()))
            .postalCode(coalesce(command.postalCode(), existing.getPostalCode()))
            .address1(coalesce(command.address1(), existing.getAddress1()))
            .address2(coalesce(command.address2(), existing.getAddress2()))
            .classificationCode(coalesce(command.classificationCode(), existing.getClassificationCode()))
            .isTradingProhibited(coalesce(command.isTradingProhibited(), existing.isTradingProhibited()))
            .isMiscellaneous(coalesce(command.isMiscellaneous(), existing.isMiscellaneous()))
            .groupCode(coalesce(command.groupCode(), existing.getGroupCode()))
            .creditLimit(coalesce(command.creditLimit(), existing.getCreditLimit()))
            .temporaryCreditIncrease(coalesce(command.temporaryCreditIncrease(),
                existing.getTemporaryCreditIncrease()))
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .build();

        partnerRepository.update(updated);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Partner> getCustomers() {
        return partnerRepository.findCustomers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Partner> getSuppliers() {
        return partnerRepository.findSuppliers();
    }

    @Override
    @Transactional(readOnly = true)
    public Partner getPartnerByCode(String partnerCode) {
        return partnerRepository.findByCode(partnerCode)
            .orElseThrow(() -> new PartnerNotFoundException(partnerCode));
    }

    @Override
    public void deletePartner(String partnerCode) {
        partnerRepository.findByCode(partnerCode)
            .orElseThrow(() -> new PartnerNotFoundException(partnerCode));

        partnerRepository.deleteByCode(partnerCode);
    }

    private <T> T coalesce(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
