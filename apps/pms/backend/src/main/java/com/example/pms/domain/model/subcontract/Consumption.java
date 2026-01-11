package com.example.pms.domain.model.subcontract;

import com.example.pms.domain.model.purchase.Receiving;
import com.example.pms.domain.model.supplier.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consumption {
    private Integer id;
    private String consumptionNumber;
    private String receivingNumber;
    private LocalDate consumptionDate;
    private String supplierCode;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // リレーション
    private Receiving receiving;
    private Supplier supplier;
    private List<ConsumptionDetail> details;
}
