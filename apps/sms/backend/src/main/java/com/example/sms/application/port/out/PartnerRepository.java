package com.example.sms.application.port.out;

import com.example.sms.domain.model.partner.Partner;

import java.util.List;
import java.util.Optional;

/**
 * 取引先リポジトリ（Output Port）.
 */
public interface PartnerRepository {

    void save(Partner partner);

    Optional<Partner> findByCode(String partnerCode);

    List<Partner> findAll();

    List<Partner> findCustomers();

    List<Partner> findSuppliers();

    void update(Partner partner);

    void deleteByCode(String partnerCode);

    void deleteAll();
}
