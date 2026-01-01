package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.partner.Partner;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * 取引先マッパー.
 */
@Mapper
public interface PartnerMapper {

    void insert(Partner partner);

    Optional<Partner> findByCode(String partnerCode);

    List<Partner> findAll();

    List<Partner> findCustomers();

    List<Partner> findSuppliers();

    void update(Partner partner);

    void deleteAll();
}
