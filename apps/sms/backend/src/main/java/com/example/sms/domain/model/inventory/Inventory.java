package com.example.sms.domain.model.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 在庫データ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private Integer id;
    private String warehouseCode;
    private String productCode;
    private String locationCode;
    private BigDecimal currentQuantity;
    private BigDecimal allocatedQuantity;
    private BigDecimal orderedQuantity;
    private LocalDate lastReceiptDate;
    private LocalDate lastShipmentDate;
    private String lotNumber;
    private String serialNumber;
    private LocalDate expirationDate;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @Builder.Default
    private Integer version = 1;

    /**
     * 有効在庫数を計算.
     */
    public BigDecimal getAvailableQuantity() {
        BigDecimal current = currentQuantity != null ? currentQuantity : BigDecimal.ZERO;
        BigDecimal allocated = allocatedQuantity != null ? allocatedQuantity : BigDecimal.ZERO;
        return current.subtract(allocated);
    }

    /**
     * 予定在庫数を計算.
     */
    public BigDecimal getProjectedQuantity() {
        BigDecimal available = getAvailableQuantity();
        BigDecimal ordered = orderedQuantity != null ? orderedQuantity : BigDecimal.ZERO;
        return available.add(ordered);
    }

    /**
     * 引当可能かどうかを判定.
     */
    public boolean canAllocate(BigDecimal quantity) {
        return getAvailableQuantity().compareTo(quantity) >= 0;
    }

    /**
     * 在庫を引当.
     */
    public void allocate(BigDecimal quantity) {
        if (!canAllocate(quantity)) {
            throw new IllegalStateException(
                "有効在庫数が不足しています。有効在庫: " + getAvailableQuantity() +
                ", 引当要求: " + quantity);
        }
        this.allocatedQuantity = this.allocatedQuantity.add(quantity);
    }

    /**
     * 引当を解除.
     */
    public void deallocate(BigDecimal quantity) {
        if (this.allocatedQuantity.compareTo(quantity) < 0) {
            throw new IllegalStateException(
                "引当数を超える解除はできません。引当数: " + this.allocatedQuantity +
                ", 解除要求: " + quantity);
        }
        this.allocatedQuantity = this.allocatedQuantity.subtract(quantity);
    }

    /**
     * 入庫処理.
     */
    public void receive(BigDecimal quantity) {
        this.currentQuantity = this.currentQuantity.add(quantity);
        this.lastReceiptDate = LocalDate.now();
    }

    /**
     * 出庫処理.
     */
    public void ship(BigDecimal quantity) {
        if (this.currentQuantity.compareTo(quantity) < 0) {
            throw new IllegalStateException(
                "現在庫数が不足しています。現在庫: " + this.currentQuantity +
                ", 出庫要求: " + quantity);
        }
        this.currentQuantity = this.currentQuantity.subtract(quantity);
        this.lastShipmentDate = LocalDate.now();
    }

    /**
     * 有効期限切れかどうか.
     */
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }
}
