package com.example.sms.application.service;

import com.example.sms.application.port.in.OrderUseCase;
import com.example.sms.application.port.in.command.CreateOrderCommand;
import com.example.sms.application.port.in.command.UpdateOrderCommand;
import com.example.sms.application.port.in.dto.OrderImportResult;
import com.example.sms.application.port.in.dto.OrderImportResult.ImportError;
import com.example.sms.application.port.out.PartnerRepository;
import com.example.sms.application.port.out.ProductRepository;
import com.example.sms.application.port.out.SalesOrderRepository;
import com.example.sms.domain.exception.OptimisticLockException;
import com.example.sms.domain.exception.SalesOrderNotFoundException;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.product.TaxCategory;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.domain.model.sales.SalesOrderDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 受注アプリケーションサービス.
 */
@Service
@Transactional
@SuppressWarnings({
    "PMD.GodClass",
    "PMD.ExcessiveImports",
    "PMD.CouplingBetweenObjects"
})
public class OrderService implements OrderUseCase {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal TAX_RATE_PERCENT = new BigDecimal("10.00");
    private static final DateTimeFormatter ORDER_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int MIN_CSV_COLUMNS = 5;

    private final SalesOrderRepository salesOrderRepository;
    private final ProductRepository productRepository;
    private final PartnerRepository partnerRepository;

    public OrderService(SalesOrderRepository salesOrderRepository,
                        ProductRepository productRepository,
                        PartnerRepository partnerRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.productRepository = productRepository;
        this.partnerRepository = partnerRepository;
    }

    @Override
    public SalesOrder createOrder(CreateOrderCommand command) {
        String orderNumber = generateOrderNumber();

        List<SalesOrderDetail> details = new ArrayList<>();
        AtomicInteger lineNumber = new AtomicInteger(1);
        BigDecimal orderAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        if (command.details() != null) {
            for (CreateOrderCommand.CreateOrderDetailCommand detailCmd : command.details()) {
                BigDecimal amount = detailCmd.unitPrice().multiply(detailCmd.orderQuantity());
                BigDecimal detailTax = amount.multiply(TAX_RATE).setScale(0, RoundingMode.DOWN);

                SalesOrderDetail detail = SalesOrderDetail.builder()
                    .lineNumber(lineNumber.getAndIncrement())
                    .productCode(detailCmd.productCode())
                    .productName(detailCmd.productName())
                    .orderQuantity(detailCmd.orderQuantity())
                    .remainingQuantity(detailCmd.orderQuantity())
                    .unit(detailCmd.unit())
                    .unitPrice(detailCmd.unitPrice())
                    .amount(amount)
                    .taxCategory(TaxCategory.EXCLUSIVE)
                    .taxRate(TAX_RATE_PERCENT)
                    .taxAmount(detailTax)
                    .warehouseCode(detailCmd.warehouseCode())
                    .requestedDeliveryDate(detailCmd.requestedDeliveryDate())
                    .remarks(detailCmd.remarks())
                    .build();

                details.add(detail);
                orderAmount = orderAmount.add(amount);
                taxAmount = taxAmount.add(detailTax);
            }
        }

        SalesOrder salesOrder = SalesOrder.builder()
            .orderNumber(orderNumber)
            .orderDate(command.orderDate() != null ? command.orderDate() : LocalDate.now())
            .customerCode(command.customerCode())
            .customerBranchNumber(command.customerBranchNumber())
            .shippingDestinationNumber(command.shippingDestinationNumber())
            .representativeCode(command.representativeCode())
            .requestedDeliveryDate(command.requestedDeliveryDate())
            .scheduledShippingDate(command.scheduledShippingDate())
            .orderAmount(orderAmount)
            .taxAmount(taxAmount)
            .totalAmount(orderAmount.add(taxAmount))
            .status(OrderStatus.RECEIVED)
            .quotationId(command.quotationId())
            .customerOrderNumber(command.customerOrderNumber())
            .remarks(command.remarks())
            .details(details)
            .build();

        salesOrderRepository.save(salesOrder);
        return salesOrder;
    }

    @Override
    public SalesOrder updateOrder(String orderNumber, UpdateOrderCommand command) {
        SalesOrder existing = salesOrderRepository.findWithDetailsByOrderNumber(orderNumber)
            .orElseThrow(() -> new SalesOrderNotFoundException(orderNumber));

        validateOptimisticLock(command.version(), existing.getVersion(), orderNumber);

        SalesOrder updated = buildUpdatedOrder(existing, command, orderNumber);

        salesOrderRepository.update(updated);
        return updated;
    }

    private void validateOptimisticLock(Integer commandVersion, Integer existingVersion, String orderNumber) {
        if (commandVersion != null && !commandVersion.equals(existingVersion)) {
            throw new OptimisticLockException("受注", orderNumber);
        }
    }

    private SalesOrder buildUpdatedOrder(SalesOrder existing, UpdateOrderCommand command, String orderNumber) {
        return SalesOrder.builder()
            .id(existing.getId())
            .orderNumber(orderNumber)
            .orderDate(existing.getOrderDate())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .shippingDestinationNumber(coalesce(command.shippingDestinationNumber(),
                existing.getShippingDestinationNumber()))
            .representativeCode(coalesce(command.representativeCode(), existing.getRepresentativeCode()))
            .requestedDeliveryDate(coalesce(command.requestedDeliveryDate(), existing.getRequestedDeliveryDate()))
            .scheduledShippingDate(coalesce(command.scheduledShippingDate(), existing.getScheduledShippingDate()))
            .orderAmount(existing.getOrderAmount())
            .taxAmount(existing.getTaxAmount())
            .totalAmount(existing.getTotalAmount())
            .status(coalesce(command.status(), existing.getStatus()))
            .quotationId(existing.getQuotationId())
            .customerOrderNumber(coalesce(command.customerOrderNumber(), existing.getCustomerOrderNumber()))
            .remarks(coalesce(command.remarks(), existing.getRemarks()))
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .version(existing.getVersion())
            .details(existing.getDetails())
            .build();
    }

    private <T> T coalesce(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrder> getAllOrders() {
        return salesOrderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<SalesOrder> getOrders(int page, int size, String keyword) {
        return salesOrderRepository.findWithPagination(page, size, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrder getOrderByNumber(String orderNumber) {
        return salesOrderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new SalesOrderNotFoundException(orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrder getOrderWithDetails(String orderNumber) {
        return salesOrderRepository.findWithDetailsByOrderNumber(orderNumber)
            .orElseThrow(() -> new SalesOrderNotFoundException(orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrder> getOrdersByStatus(OrderStatus status) {
        return salesOrderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrder> getOrdersByCustomer(String customerCode) {
        return salesOrderRepository.findByCustomerCode(customerCode);
    }

    @Override
    public void deleteOrder(String orderNumber) {
        SalesOrder existing = salesOrderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new SalesOrderNotFoundException(orderNumber));

        salesOrderRepository.deleteById(existing.getId());
    }

    @Override
    public SalesOrder cancelOrder(String orderNumber, Integer version) {
        SalesOrder existing = salesOrderRepository.findWithDetailsByOrderNumber(orderNumber)
            .orElseThrow(() -> new SalesOrderNotFoundException(orderNumber));

        if (version != null && !version.equals(existing.getVersion())) {
            throw new OptimisticLockException("受注", orderNumber);
        }

        SalesOrder cancelled = SalesOrder.builder()
            .id(existing.getId())
            .orderNumber(orderNumber)
            .orderDate(existing.getOrderDate())
            .customerCode(existing.getCustomerCode())
            .customerBranchNumber(existing.getCustomerBranchNumber())
            .shippingDestinationNumber(existing.getShippingDestinationNumber())
            .representativeCode(existing.getRepresentativeCode())
            .requestedDeliveryDate(existing.getRequestedDeliveryDate())
            .scheduledShippingDate(existing.getScheduledShippingDate())
            .orderAmount(existing.getOrderAmount())
            .taxAmount(existing.getTaxAmount())
            .totalAmount(existing.getTotalAmount())
            .status(OrderStatus.CANCELLED)
            .quotationId(existing.getQuotationId())
            .customerOrderNumber(existing.getCustomerOrderNumber())
            .remarks(existing.getRemarks())
            .createdAt(existing.getCreatedAt())
            .createdBy(existing.getCreatedBy())
            .version(existing.getVersion())
            .details(existing.getDetails())
            .build();

        salesOrderRepository.update(cancelled);
        return cancelled;
    }

    private String generateOrderNumber() {
        String datePrefix = LocalDate.now().format(ORDER_NUMBER_FORMAT);
        String prefix = "ORD-" + datePrefix + "-";

        int nextSequence = salesOrderRepository.findMaxOrderNumberByPrefix(prefix)
                .map(maxNumber -> {
                    // ORD-20260110-0001 から 0001 を抽出して +1
                    String seqPart = maxNumber.substring(prefix.length());
                    return Integer.parseInt(seqPart) + 1;
                })
                .orElse(1);

        return String.format("%s%04d", prefix, nextSequence);
    }

    @Override
    @SuppressWarnings({
        "PMD.CognitiveComplexity",
        "PMD.CyclomaticComplexity",
        "PMD.AssignmentInOperand",
        "PMD.AvoidCatchingGenericException"
    })
    public OrderImportResult importOrdersFromCsv(InputStream inputStream,
            boolean skipHeaderLine, boolean skipEmptyLines) {
        List<ImportError> errors = new ArrayList<>();
        int totalCount = 0;
        int successCount = 0;
        int skippedCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // ヘッダー行スキップ
                if (skipHeaderLine && lineNumber == 1) {
                    continue;
                }

                // 空行スキップ
                if (skipEmptyLines && line.isBlank()) {
                    skippedCount++;
                    continue;
                }

                totalCount++;

                try {
                    CsvOrderRecord record = parseCsvLine(line, lineNumber);
                    createOrderFromRecord(record);
                    successCount++;
                } catch (Exception e) {
                    errors.add(ImportError.builder()
                            .lineNumber(lineNumber)
                            .message(e.getMessage())
                            .lineContent(line.length() > 100 ? line.substring(0, 100) + "..." : line)
                            .build());
                }
            }
        } catch (java.io.IOException e) {
            errors.add(ImportError.builder()
                    .lineNumber(0)
                    .message("ファイル読み込みエラー: " + e.getMessage())
                    .build());
        }

        return OrderImportResult.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .skippedCount(skippedCount)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    /**
     * CSV 行レコード.
     */
    private record CsvOrderRecord(
            int lineNumber,
            LocalDate orderDate,
            String customerCode,
            String productCode,
            BigDecimal quantity,
            BigDecimal unitPrice,
            LocalDate requestedDeliveryDate,
            String remarks
    ) { }

    /**
     * CSV 行をパースしてレコードを生成.
     * CSV フォーマット: 受注日,顧客コード,商品コード,数量,単価,納品希望日,備考
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    private CsvOrderRecord parseCsvLine(String line, int lineNumber) {
        String[] columns = line.split(",", -1);
        if (columns.length < MIN_CSV_COLUMNS) {
            throw new IllegalArgumentException(
                    "列数が不足しています（必要: 5列以上、実際: " + columns.length + "列）");
        }

        LocalDate orderDate = parseDate(columns[0].trim(), lineNumber);
        String customerCode = columns[1].trim();
        String productCode = columns[2].trim();
        BigDecimal quantity = parseAmount(columns[3].trim(), lineNumber);
        BigDecimal unitPrice = parseAmount(columns[4].trim(), lineNumber);

        LocalDate requestedDeliveryDate = null;
        if (columns.length > MIN_CSV_COLUMNS && !columns[5].isBlank()) {
            requestedDeliveryDate = parseDate(columns[5].trim(), lineNumber);
        }

        String remarks = columns.length > MIN_CSV_COLUMNS + 1 ? columns[6].trim() : "";

        validateCsvFields(customerCode, productCode, quantity, unitPrice);

        return new CsvOrderRecord(lineNumber, orderDate, customerCode, productCode,
                quantity, unitPrice, requestedDeliveryDate, remarks);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void validateCsvFields(String customerCode, String productCode,
            BigDecimal quantity, BigDecimal unitPrice) {
        if (customerCode.isEmpty()) {
            throw new IllegalArgumentException("顧客コードは必須です");
        }
        if (productCode.isEmpty()) {
            throw new IllegalArgumentException("商品コードは必須です");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("数量は正の数で指定してください");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("単価は0以上で指定してください");
        }
    }

    private void createOrderFromRecord(CsvOrderRecord record) {
        // 顧客の存在チェック
        if (partnerRepository.findByCode(record.customerCode()).isEmpty()) {
            throw new IllegalArgumentException(
                    "顧客コード '" + record.customerCode() + "' は存在しません");
        }

        // 商品の存在チェック
        Product product = productRepository.findByCode(record.productCode())
                .orElseThrow(() -> new IllegalArgumentException(
                        "商品コード '" + record.productCode() + "' は存在しません"));

        CreateOrderCommand.CreateOrderDetailCommand detailCommand =
                new CreateOrderCommand.CreateOrderDetailCommand(
                        record.productCode(),
                        product.getProductName(), // 商品名をマスタから取得
                        record.quantity(),
                        "個", // デフォルト単位
                        record.unitPrice(),
                        null, // warehouseCode
                        record.requestedDeliveryDate(),
                        record.remarks()
                );

        CreateOrderCommand command = new CreateOrderCommand(
                record.orderDate(),
                record.customerCode(),
                null, // customerBranchNumber
                null, // shippingDestinationNumber
                null, // representativeCode
                record.requestedDeliveryDate(),
                null, // scheduledShippingDate
                null, // quotationId
                null, // customerOrderNumber
                record.remarks(),
                List.of(detailCommand)
        );

        createOrder(command);
    }

    private LocalDate parseDate(String dateStr, int lineNumber) {
        if (dateStr.isEmpty()) {
            throw new IllegalArgumentException("日付は必須です");
        }
        try {
            // yyyy/MM/dd または yyyy-MM-dd 形式をサポート
            if (dateStr.contains("/")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            } else {
                return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "日付形式が不正です（行 " + lineNumber + "）: " + dateStr, e);
        }
    }

    private BigDecimal parseAmount(String amountStr, int lineNumber) {
        if (amountStr.isEmpty()) {
            return null;
        }
        try {
            // カンマ区切りを除去
            return new BigDecimal(amountStr.replace(",", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "数値形式が不正です（行 " + lineNumber + "）: " + amountStr, e);
        }
    }
}
