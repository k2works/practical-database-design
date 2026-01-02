package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreatePartnerCommand;
import com.example.sms.application.port.in.command.UpdatePartnerCommand;
import com.example.sms.domain.model.partner.Partner;

import java.util.List;

/**
 * 取引先ユースケース（Input Port）.
 */
public interface PartnerUseCase {

    Partner createPartner(CreatePartnerCommand command);

    Partner updatePartner(String partnerCode, UpdatePartnerCommand command);

    List<Partner> getAllPartners();

    List<Partner> getCustomers();

    List<Partner> getSuppliers();

    Partner getPartnerByCode(String partnerCode);

    void deletePartner(String partnerCode);
}
