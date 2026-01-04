package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.partner.Partner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * ページネーション付きで取引先を検索.
     *
     * @param offset オフセット
     * @param limit  リミット
     * @param type   種別（customer/supplier/null）
     * @param keyword キーワード
     * @return 取引先リスト
     */
    List<Partner> findWithPagination(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("type") String type,
        @Param("keyword") String keyword);

    /**
     * 取引先の総件数を取得.
     *
     * @param type   種別（customer/supplier/null）
     * @param keyword キーワード
     * @return 総件数
     */
    long count(@Param("type") String type, @Param("keyword") String keyword);

    void update(Partner partner);

    void deleteByCode(String partnerCode);

    void deleteAll();
}
