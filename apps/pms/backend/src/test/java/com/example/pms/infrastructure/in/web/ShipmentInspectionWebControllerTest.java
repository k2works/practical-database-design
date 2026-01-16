package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.DefectUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.ShipmentInspectionUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.InspectionJudgment;
import com.example.pms.domain.model.quality.ShipmentInspection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 出荷検査実績画面コントローラーテスト.
 */
@WebMvcTest(ShipmentInspectionWebController.class)
@DisplayName("出荷検査実績画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ShipmentInspectionWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShipmentInspectionUseCase shipmentInspectionUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private DefectUseCase defectUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
        Mockito.when(defectUseCase.getAllDefects()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /shipment-inspections - 出荷検査実績一覧")
    class ListShipmentInspections {

        @Test
        @DisplayName("出荷検査実績一覧画面を表示できる")
        void shouldDisplayShipmentInspectionList() throws Exception {
            ShipmentInspection inspection = createTestShipmentInspection("SI-001", "SH-001");
            PageResult<ShipmentInspection> pageResult = new PageResult<>(List.of(inspection), 0, 20, 1);
            Mockito.when(shipmentInspectionUseCase.getShipmentInspectionList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/shipment-inspections"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipment-inspections/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("inspectionList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            ShipmentInspection inspection = createTestShipmentInspection("SI-001", "SH-001");
            PageResult<ShipmentInspection> pageResult = new PageResult<>(List.of(inspection), 0, 20, 1);
            Mockito.when(shipmentInspectionUseCase.getShipmentInspectionList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("SH-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/shipment-inspections")
                    .param("keyword", "SH-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipment-inspections/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "SH-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            ShipmentInspection inspection = createTestShipmentInspection("SI-001", "SH-001");
            PageResult<ShipmentInspection> pageResult = new PageResult<>(List.of(inspection), 1, 10, 25);
            Mockito.when(shipmentInspectionUseCase.getShipmentInspectionList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/shipment-inspections")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /shipment-inspections/new - 出荷検査実績登録画面")
    class NewShipmentInspection {

        @Test
        @DisplayName("出荷検査実績登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/shipment-inspections/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipment-inspections/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"));
        }
    }

    @Nested
    @DisplayName("POST /shipment-inspections - 出荷検査実績登録処理")
    class CreateShipmentInspection {

        @Test
        @DisplayName("出荷検査実績を登録できる")
        void shouldCreateShipmentInspection() throws Exception {
            ShipmentInspection created = createTestShipmentInspection("SI-001", "SH-001");
            Mockito.when(shipmentInspectionUseCase.createShipmentInspection(
                    ArgumentMatchers.any(ShipmentInspection.class)))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/shipment-inspections")
                    .param("shipmentNumber", "SH-001")
                    .param("itemCode", "ITEM-001")
                    .param("inspectionDate", "2025-01-14")
                    .param("inspectorCode", "STAFF-001")
                    .param("inspectionQuantity", "100")
                    .param("passedQuantity", "95")
                    .param("failedQuantity", "5")
                    .param("judgment", "PASSED"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/shipment-inspections"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/shipment-inspections")
                    .param("shipmentNumber", "")
                    .param("itemCode", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipment-inspections/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "shipmentNumber"));
        }
    }

    @Nested
    @DisplayName("GET /shipment-inspections/{inspectionNumber} - 出荷検査実績詳細画面")
    class ShowShipmentInspection {

        @Test
        @DisplayName("出荷検査実績詳細画面を表示できる")
        void shouldDisplayShipmentInspectionDetail() throws Exception {
            ShipmentInspection inspection = createTestShipmentInspection("SI-001", "SH-001");
            Mockito.when(shipmentInspectionUseCase.getShipmentInspection("SI-001"))
                .thenReturn(Optional.of(inspection));

            mockMvc.perform(MockMvcRequestBuilders.get("/shipment-inspections/SI-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipment-inspections/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("inspection"));
        }

        @Test
        @DisplayName("出荷検査実績が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(shipmentInspectionUseCase.getShipmentInspection("SI-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/shipment-inspections/SI-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/shipment-inspections"));
        }
    }

    @Nested
    @DisplayName("GET /shipment-inspections/{inspectionNumber}/edit - 出荷検査実績編集画面")
    class EditShipmentInspection {

        @Test
        @DisplayName("出荷検査実績編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            ShipmentInspection inspection = createTestShipmentInspection("SI-001", "SH-001");
            Mockito.when(shipmentInspectionUseCase.getShipmentInspection("SI-001"))
                .thenReturn(Optional.of(inspection));

            mockMvc.perform(MockMvcRequestBuilders.get("/shipment-inspections/SI-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("shipment-inspections/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"));
        }

        @Test
        @DisplayName("出荷検査実績が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(shipmentInspectionUseCase.getShipmentInspection("SI-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/shipment-inspections/SI-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/shipment-inspections"));
        }
    }

    @Nested
    @DisplayName("POST /shipment-inspections/{inspectionNumber} - 出荷検査実績更新処理")
    class UpdateShipmentInspection {

        @Test
        @DisplayName("出荷検査実績を更新できる")
        void shouldUpdateShipmentInspection() throws Exception {
            ShipmentInspection updated = createTestShipmentInspection("SI-001", "SH-001");
            Mockito.when(shipmentInspectionUseCase.updateShipmentInspection(
                    ArgumentMatchers.eq("SI-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/shipment-inspections/SI-001")
                    .param("inspectionNumber", "SI-001")
                    .param("shipmentNumber", "SH-001")
                    .param("itemCode", "ITEM-001")
                    .param("inspectionDate", "2025-01-14")
                    .param("inspectorCode", "STAFF-001")
                    .param("inspectionQuantity", "100")
                    .param("passedQuantity", "90")
                    .param("failedQuantity", "10")
                    .param("judgment", "FAILED"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/shipment-inspections"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /shipment-inspections/{inspectionNumber}/delete - 出荷検査実績削除処理")
    class DeleteShipmentInspection {

        @Test
        @DisplayName("出荷検査実績を削除できる")
        void shouldDeleteShipmentInspection() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/shipment-inspections/SI-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/shipment-inspections"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(shipmentInspectionUseCase).deleteShipmentInspection("SI-001");
        }
    }

    private ShipmentInspection createTestShipmentInspection(String inspectionNumber, String shipmentNumber) {
        return ShipmentInspection.builder()
            .id(1)
            .inspectionNumber(inspectionNumber)
            .shipmentNumber(shipmentNumber)
            .itemCode("ITEM-001")
            .inspectionDate(LocalDate.of(2025, 1, 14))
            .inspectorCode("STAFF-001")
            .inspectionQuantity(new BigDecimal("100"))
            .passedQuantity(new BigDecimal("95"))
            .failedQuantity(new BigDecimal("5"))
            .judgment(InspectionJudgment.PASSED)
            .build();
    }
}
