package com.example.sms.infrastructure.out.persistence.mapper;

import com.example.sms.domain.model.partner.ShippingDestination;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 出荷先マッパー.
 */
@Mapper
public interface ShippingDestinationMapper {

    void insert(ShippingDestination shippingDestination);

    Optional<ShippingDestination> findByKey(@Param("partnerCode") String partnerCode,
                                            @Param("customerBranchNumber") String customerBranchNumber,
                                            @Param("shippingNumber") String shippingNumber);

    List<ShippingDestination> findByCustomer(@Param("partnerCode") String partnerCode,
                                             @Param("customerBranchNumber") String customerBranchNumber);

    List<ShippingDestination> findAll();

    void update(ShippingDestination shippingDestination);

    void deleteByKey(@Param("partnerCode") String partnerCode,
                     @Param("customerBranchNumber") String customerBranchNumber,
                     @Param("shippingNumber") String shippingNumber);

    void deleteByCustomer(@Param("partnerCode") String partnerCode,
                          @Param("customerBranchNumber") String customerBranchNumber);

    void deleteAll();
}
