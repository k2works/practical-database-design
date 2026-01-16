package com.example.pms.application.service;

import com.example.pms.application.port.in.SupplierUseCase;
import com.example.pms.application.port.in.command.CreateSupplierCommand;
import com.example.pms.application.port.in.command.UpdateSupplierCommand;
import com.example.pms.application.port.out.SupplierRepository;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.supplier.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 取引先サービス（Application Service）.
 */
@Service
@Transactional
public class SupplierService implements SupplierUseCase {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Supplier> getSuppliers(int page, int size, String keyword) {
        int offset = page * size;
        List<Supplier> suppliers = supplierRepository.findWithPagination(keyword, size, offset);
        long totalElements = supplierRepository.count(keyword);
        return new PageResult<>(suppliers, page, size, totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier createSupplier(CreateSupplierCommand command) {
        Supplier supplier = Supplier.builder()
            .supplierCode(command.getSupplierCode())
            .effectiveFrom(command.getEffectiveFrom())
            .effectiveTo(command.getEffectiveTo())
            .supplierName(command.getSupplierName())
            .supplierNameKana(command.getSupplierNameKana())
            .supplierType(command.getSupplierType())
            .postalCode(command.getPostalCode())
            .address(command.getAddress())
            .phoneNumber(command.getPhoneNumber())
            .faxNumber(command.getFaxNumber())
            .contactPerson(command.getContactPerson())
            .build();
        supplierRepository.save(supplier);
        return supplier;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplier(String supplierCode, LocalDate effectiveFrom) {
        return supplierRepository.findBySupplierCodeAndEffectiveFrom(supplierCode, effectiveFrom);
    }

    @Override
    public Supplier updateSupplier(String supplierCode, LocalDate effectiveFrom, UpdateSupplierCommand command) {
        Supplier supplier = Supplier.builder()
            .supplierCode(supplierCode)
            .effectiveFrom(effectiveFrom)
            .effectiveTo(command.getEffectiveTo())
            .supplierName(command.getSupplierName())
            .supplierNameKana(command.getSupplierNameKana())
            .supplierType(command.getSupplierType())
            .postalCode(command.getPostalCode())
            .address(command.getAddress())
            .phoneNumber(command.getPhoneNumber())
            .faxNumber(command.getFaxNumber())
            .contactPerson(command.getContactPerson())
            .build();
        supplierRepository.update(supplier);
        return supplier;
    }

    @Override
    public void deleteSupplier(String supplierCode, LocalDate effectiveFrom) {
        supplierRepository.deleteBySupplierCodeAndEffectiveFrom(supplierCode, effectiveFrom);
    }
}
