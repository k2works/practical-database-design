package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.domain.model.partner.Partner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * 取引先マスタ画面コントローラーテスト.
 */
@WebMvcTest(PartnerWebController.class)
@DisplayName("取引先マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class PartnerWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PartnerUseCase partnerUseCase;

    @Nested
    @DisplayName("GET /partners")
    class ListPartners {

        @Test
        @DisplayName("取引先一覧画面を表示できる")
        void shouldDisplayPartnerList() throws Exception {
            Partner partner = createTestPartner("WEB-P001", "Webテスト取引先", true, false);
            when(partnerUseCase.getAllPartners()).thenReturn(List.of(partner));

            mockMvc.perform(MockMvcRequestBuilders.get("/partners"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("partners/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("partners"));
        }

        @Test
        @DisplayName("顧客タイプでフィルタできる")
        void shouldFilterByCustomerType() throws Exception {
            Partner customer = createTestPartner("WEB-C001", "顧客", true, false);
            when(partnerUseCase.getCustomers()).thenReturn(List.of(customer));

            mockMvc.perform(MockMvcRequestBuilders.get("/partners")
                    .param("type", "customer"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("partners/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedType", "customer"));
        }

        @Test
        @DisplayName("仕入先タイプでフィルタできる")
        void shouldFilterBySupplierType() throws Exception {
            Partner supplier = createTestPartner("WEB-S002", "仕入先", false, true);
            when(partnerUseCase.getSuppliers()).thenReturn(List.of(supplier));

            mockMvc.perform(MockMvcRequestBuilders.get("/partners")
                    .param("type", "supplier"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("partners/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedType", "supplier"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Partner partner = createTestPartner("WEB-P002", "検索テスト取引先", true, false);
            when(partnerUseCase.getAllPartners()).thenReturn(List.of(partner));

            mockMvc.perform(MockMvcRequestBuilders.get("/partners")
                    .param("keyword", "検索"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("partners/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "検索"));
        }
    }

    @Nested
    @DisplayName("GET /partners/{partnerCode}")
    class ShowPartner {

        @Test
        @DisplayName("取引先詳細画面を表示できる")
        void shouldDisplayPartnerDetail() throws Exception {
            Partner partner = createTestPartner("WEB-P003", "詳細取引先", true, false);
            when(partnerUseCase.getPartnerByCode("WEB-P003")).thenReturn(partner);

            mockMvc.perform(MockMvcRequestBuilders.get("/partners/WEB-P003"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("partners/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("partner"));
        }
    }

    @Nested
    @DisplayName("GET /partners/new")
    class NewPartnerForm {

        @Test
        @DisplayName("取引先登録フォームを表示できる")
        void shouldDisplayNewPartnerForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/partners/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("partners/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /partners")
    class CreatePartner {

        @Test
        @DisplayName("取引先を登録できる")
        void shouldCreatePartner() throws Exception {
            Partner created = createTestPartner("NEW-WEB-P001", "新規取引先", true, false);
            when(partnerUseCase.createPartner(any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/partners")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("partnerCode", "NEW-WEB-P001")
                    .param("partnerName", "新規取引先")
                    .param("partnerNameKana", "シンキトリヒキサキ")
                    .param("isCustomer", "true")
                    .param("isSupplier", "false")
                    .param("postalCode", "100-0001")
                    .param("address1", "東京都千代田区")
                    .param("creditLimit", "1000000")
                    .param("temporaryCreditIncrease", "0"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/partners"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は登録フォームに戻る")
        void shouldReturnToFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/partners")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("partnerCode", "")
                    .param("partnerName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("partners/new"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
        }
    }

    @Nested
    @DisplayName("GET /partners/{partnerCode}/edit")
    class EditPartnerForm {

        @Test
        @DisplayName("取引先編集フォームを表示できる")
        void shouldDisplayEditPartnerForm() throws Exception {
            Partner partner = createTestPartner("WEB-EDIT-P001", "編集取引先", true, false);
            when(partnerUseCase.getPartnerByCode("WEB-EDIT-P001")).thenReturn(partner);

            mockMvc.perform(MockMvcRequestBuilders.get("/partners/WEB-EDIT-P001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("partners/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /partners/{partnerCode}")
    class UpdatePartner {

        @Test
        @DisplayName("取引先を更新できる")
        void shouldUpdatePartner() throws Exception {
            Partner updated = createTestPartner("WEB-UPD-P001", "更新後取引先", true, false);
            when(partnerUseCase.updatePartner(anyString(), any())).thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/partners/WEB-UPD-P001")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("partnerCode", "WEB-UPD-P001")
                    .param("partnerName", "更新後取引先")
                    .param("partnerNameKana", "コウシンゴトリヒキサキ")
                    .param("isCustomer", "true")
                    .param("isSupplier", "false")
                    .param("creditLimit", "2000000")
                    .param("temporaryCreditIncrease", "0"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/partners/WEB-UPD-P001"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /partners/{partnerCode}/delete")
    class DeletePartner {

        @Test
        @DisplayName("取引先を削除できる")
        void shouldDeletePartner() throws Exception {
            doNothing().when(partnerUseCase).deletePartner("WEB-DEL-P001");

            mockMvc.perform(MockMvcRequestBuilders.post("/partners/WEB-DEL-P001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/partners"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    private Partner createTestPartner(String partnerCode, String partnerName,
                                       boolean isCustomer, boolean isSupplier) {
        return Partner.builder()
            .partnerCode(partnerCode)
            .partnerName(partnerName)
            .partnerNameKana("テストトリヒキサキ")
            .isCustomer(isCustomer)
            .isSupplier(isSupplier)
            .postalCode("100-0001")
            .address1("東京都千代田区")
            .isTradingProhibited(false)
            .isMiscellaneous(false)
            .creditLimit(new BigDecimal("1000000"))
            .temporaryCreditIncrease(BigDecimal.ZERO)
            .build();
    }
}
