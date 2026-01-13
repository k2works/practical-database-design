package com.example.pms.infrastructure.in.rest.dto;

import com.example.pms.domain.model.purchase.PurchaseOrder;
import com.example.pms.domain.model.purchase.PurchaseOrderDetail;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 発注レスポンス DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {
    Integer id;
    String purchaseOrderNumber;
    LocalDate orderDate;
    String supplierCode;
    String ordererCode;
    String departmentCode;
    PurchaseOrderStatus status;
    String statusDisplayName;
    String remarks;
    List<PurchaseOrderDetailResponse> details;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    /**
     * 発注明細レスポンス DTO.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseOrderDetailResponse {
        Integer id;
        Integer lineNumber;
        String itemCode;
        String deliveryLocationCode;
        LocalDate expectedReceivingDate;
        LocalDate confirmedDeliveryDate;
        BigDecimal orderQuantity;
        BigDecimal orderUnitPrice;
        BigDecimal orderAmount;
        BigDecimal receivedQuantity;
        BigDecimal acceptedQuantity;
        Boolean completedFlag;
        String detailRemarks;

        /**
         * ドメインモデルからレスポンスを作成する.
         *
         * @param detail 発注明細
         * @return PurchaseOrderDetailResponse
         */
        public static PurchaseOrderDetailResponse from(PurchaseOrderDetail detail) {
            return PurchaseOrderDetailResponse.builder()
                .id(detail.getId())
                .lineNumber(detail.getLineNumber())
                .itemCode(detail.getItemCode())
                .deliveryLocationCode(detail.getDeliveryLocationCode())
                .expectedReceivingDate(detail.getExpectedReceivingDate())
                .confirmedDeliveryDate(detail.getConfirmedDeliveryDate())
                .orderQuantity(detail.getOrderQuantity())
                .orderUnitPrice(detail.getOrderUnitPrice())
                .orderAmount(detail.getOrderAmount())
                .receivedQuantity(detail.getReceivedQuantity())
                .acceptedQuantity(detail.getAcceptedQuantity())
                .completedFlag(detail.getCompletedFlag())
                .detailRemarks(detail.getDetailRemarks())
                .build();
        }
    }

    /**
     * ドメインモデルからレスポンスを作成する.
     *
     * @param order 発注
     * @return PurchaseOrderResponse
     */
    public static PurchaseOrderResponse from(PurchaseOrder order) {
        List<PurchaseOrderDetailResponse> detailResponses = null;
        if (order.getDetails() != null) {
            detailResponses = order.getDetails().stream()
                .map(PurchaseOrderDetailResponse::from)
                .toList();
        }

        return PurchaseOrderResponse.builder()
            .id(order.getId())
            .purchaseOrderNumber(order.getPurchaseOrderNumber())
            .orderDate(order.getOrderDate())
            .supplierCode(order.getSupplierCode())
            .ordererCode(order.getOrdererCode())
            .departmentCode(order.getDepartmentCode())
            .status(order.getStatus())
            .statusDisplayName(order.getStatus() != null ? order.getStatus().getDisplayName() : null)
            .remarks(order.getRemarks())
            .details(detailResponses)
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}
