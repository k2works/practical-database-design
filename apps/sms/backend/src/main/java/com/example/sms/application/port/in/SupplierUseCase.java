package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateSupplierCommand;
import com.example.sms.application.port.in.command.UpdateSupplierCommand;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Supplier;

import java.util.List;

/**
 * 仕入先ユースケース（Input Port）.
 */
public interface SupplierUseCase {

    Supplier createSupplier(CreateSupplierCommand command);

    Supplier updateSupplier(String supplierCode, String branchNumber, UpdateSupplierCommand command);

    List<Supplier> getAllSuppliers();

    /**
     * ページネーション付きで仕入先を取得.
     */
    PageResult<Supplier> getSuppliers(int page, int size, String keyword);

    List<Supplier> getSuppliersByCode(String supplierCode);

    Supplier getSupplierByCodeAndBranch(String supplierCode, String branchNumber);

    void deleteSupplier(String supplierCode, String branchNumber);
}
