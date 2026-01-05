package com.example.sms.infrastructure.in.rest.controller;

import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.command.CreatePartnerCommand;
import com.example.sms.application.port.in.command.UpdatePartnerCommand;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.infrastructure.in.rest.dto.CreatePartnerRequest;
import com.example.sms.infrastructure.in.rest.dto.PartnerResponse;
import com.example.sms.infrastructure.in.rest.dto.UpdatePartnerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 取引先マスタ API コントローラー.
 */
@RestController
@RequestMapping("/api/v1/partners")
@Tag(name = "partners", description = "取引先マスタ API")
public class PartnerController {

    private static final String TYPE_CUSTOMER = "customer";
    private static final String TYPE_SUPPLIER = "supplier";

    private final PartnerUseCase partnerUseCase;

    public PartnerController(PartnerUseCase partnerUseCase) {
        this.partnerUseCase = partnerUseCase;
    }

    @GetMapping
    @Operation(summary = "取引先一覧の取得", description = "すべての取引先を取得します。typeパラメータで顧客/仕入先をフィルタできます")
    @ApiResponse(responseCode = "200", description = "取引先一覧を返却")
    public ResponseEntity<List<PartnerResponse>> getAllPartners(
            @Parameter(description = "取引先タイプ（customer: 顧客, supplier: 仕入先）")
            @RequestParam(required = false) String type) {

        List<Partner> partners;
        if (TYPE_CUSTOMER.equals(type)) {
            partners = partnerUseCase.getCustomers();
        } else if (TYPE_SUPPLIER.equals(type)) {
            partners = partnerUseCase.getSuppliers();
        } else {
            partners = partnerUseCase.getAllPartners();
        }

        List<PartnerResponse> responses = partners.stream()
            .map(PartnerResponse::from)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{partnerCode}")
    @Operation(summary = "取引先の取得", description = "取引先コードを指定して取引先情報を取得します")
    @ApiResponse(responseCode = "200", description = "取引先を返却")
    @ApiResponse(responseCode = "404", description = "取引先が見つからない")
    public ResponseEntity<PartnerResponse> getPartner(
            @Parameter(description = "取引先コード", example = "PTN-001")
            @PathVariable String partnerCode) {

        Partner partner = partnerUseCase.getPartnerByCode(partnerCode);
        return ResponseEntity.ok(PartnerResponse.from(partner));
    }

    @PostMapping
    @Operation(summary = "取引先の登録", description = "新規取引先を登録します")
    @ApiResponse(responseCode = "201", description = "取引先を登録")
    @ApiResponse(responseCode = "409", description = "取引先が既に存在する")
    public ResponseEntity<PartnerResponse> createPartner(
            @Valid @RequestBody CreatePartnerRequest request) {

        CreatePartnerCommand command = new CreatePartnerCommand(
            request.partnerCode(),
            request.partnerName(),
            request.partnerNameKana(),
            request.isCustomerValue(),
            request.isSupplierValue(),
            request.postalCode(),
            request.address1(),
            request.address2(),
            request.classificationCode(),
            request.isTradingProhibitedValue(),
            request.isMiscellaneousValue(),
            request.groupCode(),
            request.creditLimit() != null ? request.creditLimit() : BigDecimal.ZERO,
            request.temporaryCreditIncrease() != null ? request.temporaryCreditIncrease() : BigDecimal.ZERO
        );

        Partner partner = partnerUseCase.createPartner(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PartnerResponse.from(partner));
    }

    @PutMapping("/{partnerCode}")
    @Operation(summary = "取引先の更新", description = "取引先コードを指定して取引先情報を更新します")
    @ApiResponse(responseCode = "200", description = "取引先を更新")
    @ApiResponse(responseCode = "404", description = "取引先が見つからない")
    public ResponseEntity<PartnerResponse> updatePartner(
            @Parameter(description = "取引先コード", example = "PTN-001")
            @PathVariable String partnerCode,
            @Valid @RequestBody UpdatePartnerRequest request) {

        UpdatePartnerCommand command = new UpdatePartnerCommand(
            request.partnerName(),
            request.partnerNameKana(),
            request.isCustomer(),
            request.isSupplier(),
            request.postalCode(),
            request.address1(),
            request.address2(),
            request.classificationCode(),
            request.isTradingProhibited(),
            request.isMiscellaneous(),
            request.groupCode(),
            request.creditLimit(),
            request.temporaryCreditIncrease()
        );

        Partner partner = partnerUseCase.updatePartner(partnerCode, command);
        return ResponseEntity.ok(PartnerResponse.from(partner));
    }

    @DeleteMapping("/{partnerCode}")
    @Operation(summary = "取引先の削除", description = "取引先コードを指定して取引先を削除します")
    @ApiResponse(responseCode = "204", description = "取引先を削除")
    @ApiResponse(responseCode = "404", description = "取引先が見つからない")
    public ResponseEntity<Void> deletePartner(
            @Parameter(description = "取引先コード", example = "PTN-001")
            @PathVariable String partnerCode) {

        partnerUseCase.deletePartner(partnerCode);
        return ResponseEntity.noContent().build();
    }
}
