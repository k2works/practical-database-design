package com.example.sms.application.service;

import com.example.sms.application.port.in.SupplierUseCase;
import com.example.sms.application.port.in.command.CreateSupplierCommand;
import com.example.sms.application.port.in.command.UpdateSupplierCommand;
import com.example.sms.application.port.out.SupplierRepository;
import com.example.sms.domain.exception.DuplicateSupplierException;
import com.example.sms.domain.exception.SupplierNotFoundException;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 仕入先アプリケーションサービス.
 */
@Service
@Transactional
public class SupplierService implements SupplierUseCase {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Supplier createSupplier(CreateSupplierCommand command) {
        String branchNumber = command.supplierBranchNumber() != null
            ? command.supplierBranchNumber() : "00";

        supplierRepository.findByCodeAndBranch(command.supplierCode(), branchNumber)
            .ifPresent(existing -> {
                throw new DuplicateSupplierException(command.supplierCode(), branchNumber);
            });

        Supplier supplier = Supplier.builder()
            .supplierCode(command.supplierCode())
            .supplierBranchNumber(branchNumber)
            .representativeName(command.representativeName())
            .departmentName(command.departmentName())
            .phone(command.phone())
            .fax(command.fax())
            .email(command.email())
            .build();

        supplierRepository.save(supplier);
        return supplier;
    }

    @Override
    public Supplier updateSupplier(String supplierCode, String branchNumber, UpdateSupplierCommand command) {
        Supplier existing = supplierRepository.findByCodeAndBranch(supplierCode, branchNumber)
            .orElseThrow(() -> new SupplierNotFoundException(supplierCode, branchNumber));

        Supplier updated = Supplier.builder()
            .supplierCode(supplierCode)
            .supplierBranchNumber(branchNumber)
            .representativeName(coalesce(command.representativeName(), existing.getRepresentativeName()))
            .departmentName(coalesce(command.departmentName(), existing.getDepartmentName()))
            .phone(coalesce(command.phone(), existing.getPhone()))
            .fax(coalesce(command.fax(), existing.getFax()))
            .email(coalesce(command.email(), existing.getEmail()))
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .build();

        supplierRepository.update(updated);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Supplier> getSuppliers(int page, int size, String keyword) {
        return supplierRepository.findWithPagination(page, size, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getSuppliersByCode(String supplierCode) {
        return supplierRepository.findByCode(supplierCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Supplier getSupplierByCodeAndBranch(String supplierCode, String branchNumber) {
        return supplierRepository.findByCodeAndBranch(supplierCode, branchNumber)
            .orElseThrow(() -> new SupplierNotFoundException(supplierCode, branchNumber));
    }

    @Override
    public void deleteSupplier(String supplierCode, String branchNumber) {
        supplierRepository.findByCodeAndBranch(supplierCode, branchNumber)
            .orElseThrow(() -> new SupplierNotFoundException(supplierCode, branchNumber));

        supplierRepository.deleteByCodeAndBranch(supplierCode, branchNumber);
    }

    private <T> T coalesce(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
