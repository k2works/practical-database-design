package com.example.sms.application.port.out;

import com.example.sms.domain.model.common.PageResult;
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

    /**
     * ページネーション付きで取引先を検索.
     *
     * @param page    ページ番号（0始まり）
     * @param size    ページサイズ
     * @param type    種別（customer/supplier/null）
     * @param keyword キーワード
     * @return ページネーション結果
     */
    PageResult<Partner> findWithPagination(int page, int size, String type, String keyword);

    void update(Partner partner);

    void deleteByCode(String partnerCode);

    void deleteAll();
}
