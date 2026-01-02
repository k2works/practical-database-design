package com.example.sms.infrastructure.out.persistence.repository;

import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.infrastructure.out.persistence.mapper.PartnerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 取引先リポジトリ実装.
 */
@Repository
@RequiredArgsConstructor
public class PartnerRepositoryImpl implements PartnerRepository {

    private final PartnerMapper partnerMapper;

    @Override
    public void save(Partner partner) {
        partnerMapper.insert(partner);
    }

    @Override
    public Optional<Partner> findByCode(String partnerCode) {
        return partnerMapper.findByCode(partnerCode);
    }

    @Override
    public List<Partner> findAll() {
        return partnerMapper.findAll();
    }

    @Override
    public List<Partner> findCustomers() {
        return partnerMapper.findCustomers();
    }

    @Override
    public List<Partner> findSuppliers() {
        return partnerMapper.findSuppliers();
    }

    @Override
    public void update(Partner partner) {
        partnerMapper.update(partner);
    }

    @Override
    public void deleteByCode(String partnerCode) {
        partnerMapper.deleteByCode(partnerCode);
    }

    @Override
    public void deleteAll() {
        partnerMapper.deleteAll();
    }
}
