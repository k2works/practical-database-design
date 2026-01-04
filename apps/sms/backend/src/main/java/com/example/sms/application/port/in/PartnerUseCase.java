package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreatePartnerCommand;
import com.example.sms.application.port.in.command.UpdatePartnerCommand;
import com.example.sms.domain.model.common.PageResult;
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

    /**
     * ページネーション付きで取引先を取得.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param type    種別（customer/supplier/null）
     * @param keyword キーワード
     * @return ページネーション結果
     */
    PageResult<Partner> getPartners(int page, int size, String type, String keyword);

    Partner getPartnerByCode(String partnerCode);

    void deletePartner(String partnerCode);
}
