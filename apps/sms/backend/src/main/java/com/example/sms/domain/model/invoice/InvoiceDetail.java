package com.example.sms.domain.model.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 請求明細エンティティ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetail {
    private Integer id;
    private Integer invoiceId;
    private Integer lineNumber;
    private Integer salesId;
    private String salesNumber;
    private LocalDate salesDate;
    private BigDecimal salesAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
}
