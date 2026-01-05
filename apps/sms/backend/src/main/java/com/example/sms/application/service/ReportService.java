package com.example.sms.application.service;

import com.example.sms.application.port.in.ReportUseCase;
import com.example.sms.infrastructure.in.web.dto.InventoryReportData;
import com.example.sms.application.port.out.InventoryRepository;
import com.example.sms.application.port.out.InvoiceRepository;
import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.application.port.out.PurchaseOrderRepository;
import com.example.sms.application.port.out.QuotationRepository;
import com.example.sms.application.port.out.WarehouseRepository;
import com.example.sms.domain.exception.InvoiceNotFoundException;
import com.example.sms.domain.exception.PurchaseOrderNotFoundException;
import com.example.sms.domain.exception.QuotationNotFoundException;
import com.example.sms.domain.model.inventory.Inventory;
import com.example.sms.domain.model.inventory.Warehouse;
import com.example.sms.domain.model.invoice.Invoice;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.purchase.PurchaseOrder;
import com.example.sms.domain.model.sales.Quotation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 帳票アプリケーションサービス.
 */
@Service
@Transactional(readOnly = true)
public class ReportService implements ReportUseCase {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InvoiceRepository invoiceRepository;
    private final PartnerRepository partnerRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final QuotationRepository quotationRepository;

    public ReportService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            WarehouseRepository warehouseRepository,
            InvoiceRepository invoiceRepository,
            PartnerRepository partnerRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            QuotationRepository quotationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.invoiceRepository = invoiceRepository;
        this.partnerRepository = partnerRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.quotationRepository = quotationRepository;
    }

    @Override
    public List<InventoryReportData> getInventoryReport() {
        List<Inventory> inventories = inventoryRepository.findAll();

        Map<String, Product> productMap = productRepository.findAll().stream()
            .collect(Collectors.toMap(Product::getProductCode, p -> p));

        Map<String, Warehouse> warehouseMap = warehouseRepository.findAll().stream()
            .collect(Collectors.toMap(Warehouse::getWarehouseCode, w -> w));

        return inventories.stream()
            .map(inventory -> {
                Product product = productMap.get(inventory.getProductCode());
                Warehouse warehouse = warehouseMap.get(inventory.getWarehouseCode());

                return InventoryReportData.builder()
                    .warehouseCode(inventory.getWarehouseCode())
                    .warehouseName(warehouse != null ? warehouse.getWarehouseName() : "")
                    .productCode(inventory.getProductCode())
                    .productName(product != null ? product.getProductName() : "")
                    .currentStock(inventory.getCurrentQuantity())
                    .allocatedStock(inventory.getAllocatedQuantity())
                    .availableStock(inventory.getAvailableQuantity())
                    .locationCode(inventory.getLocationCode())
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Override
    public Invoice getInvoiceForReport(String invoiceNumber) {
        return invoiceRepository.findWithDetailsByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new InvoiceNotFoundException(invoiceNumber));
    }

    /**
     * 顧客コードから顧客名を取得する.
     *
     * @param customerCode 顧客コード
     * @return 顧客名（見つからない場合は空文字列）
     */
    public String getCustomerName(String customerCode) {
        if (customerCode == null || customerCode.isEmpty()) {
            return "";
        }
        return partnerRepository.findByCode(customerCode)
            .map(Partner::getPartnerName)
            .orElse("");
    }

    @Override
    public PurchaseOrder getPurchaseOrderForReport(String purchaseOrderNumber) {
        return purchaseOrderRepository.findWithDetailsByPurchaseOrderNumber(purchaseOrderNumber)
            .orElseThrow(() -> new PurchaseOrderNotFoundException(purchaseOrderNumber));
    }

    /**
     * 仕入先コードから仕入先名を取得する.
     *
     * @param supplierCode 仕入先コード
     * @return 仕入先名（見つからない場合は空文字列）
     */
    public String getSupplierName(String supplierCode) {
        if (supplierCode == null || supplierCode.isEmpty()) {
            return "";
        }
        return partnerRepository.findByCode(supplierCode)
            .map(Partner::getPartnerName)
            .orElse("");
    }

    @Override
    public Quotation getQuotationForReport(String quotationNumber) {
        return quotationRepository.findWithDetailsByQuotationNumber(quotationNumber)
            .orElseThrow(() -> new QuotationNotFoundException(quotationNumber));
    }
}
