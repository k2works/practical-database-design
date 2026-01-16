package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.LotMasterUseCase;
import com.example.pms.application.port.in.command.CreateLotMasterCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.quality.LotMaster;
import com.example.pms.domain.model.quality.LotType;
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
 * ロットマスタ画面コントローラーテスト.
 */
@WebMvcTest(LotMasterWebController.class)
@DisplayName("ロットマスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class LotMasterWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LotMasterUseCase lotMasterUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /lots - ロットマスタ一覧")
    class ListLots {

        @Test
        @DisplayName("ロットマスタ一覧画面を表示できる")
        void shouldDisplayLotMasterList() throws Exception {
            LotMaster lot = createTestLotMaster("LOT-001", "ITEM-001");
            PageResult<LotMaster> pageResult = new PageResult<>(List.of(lot), 0, 20, 1);
            Mockito.when(lotMasterUseCase.getLotMasterList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/lots"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("lots/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lotList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            LotMaster lot = createTestLotMaster("LOT-001", "ITEM-001");
            PageResult<LotMaster> pageResult = new PageResult<>(List.of(lot), 0, 20, 1);
            Mockito.when(lotMasterUseCase.getLotMasterList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("LOT-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/lots")
                    .param("keyword", "LOT-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("lots/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "LOT-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            LotMaster lot = createTestLotMaster("LOT-001", "ITEM-001");
            PageResult<LotMaster> pageResult = new PageResult<>(List.of(lot), 1, 10, 25);
            Mockito.when(lotMasterUseCase.getLotMasterList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/lots")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /lots/new - ロットマスタ登録画面")
    class NewLotMaster {

        @Test
        @DisplayName("ロットマスタ登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/lots/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("lots/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lotTypes"));
        }
    }

    @Nested
    @DisplayName("POST /lots - ロットマスタ登録処理")
    class CreateLotMaster {

        @Test
        @DisplayName("ロットマスタを登録できる")
        void shouldCreateLotMaster() throws Exception {
            LotMaster created = createTestLotMaster("LOT-001", "ITEM-001");
            Mockito.when(lotMasterUseCase.createLotMaster(
                    ArgumentMatchers.any(CreateLotMasterCommand.class)))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/lots")
                    .param("lotNumber", "LOT-001")
                    .param("itemCode", "ITEM-001")
                    .param("lotType", "PURCHASED")
                    .param("manufactureDate", "2025-01-01")
                    .param("quantity", "100"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/lots"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/lots")
                    .param("lotNumber", "")
                    .param("itemCode", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("lots/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "lotNumber"));
        }
    }

    @Nested
    @DisplayName("GET /lots/{lotNumber} - ロットマスタ詳細画面")
    class ShowLotMaster {

        @Test
        @DisplayName("ロットマスタ詳細画面を表示できる")
        void shouldDisplayLotMasterDetail() throws Exception {
            LotMaster lot = createTestLotMaster("LOT-001", "ITEM-001");
            Mockito.when(lotMasterUseCase.getLotMaster("LOT-001"))
                .thenReturn(Optional.of(lot));

            mockMvc.perform(MockMvcRequestBuilders.get("/lots/LOT-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("lots/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lot"));
        }

        @Test
        @DisplayName("ロットマスタが見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(lotMasterUseCase.getLotMaster("LOT-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/lots/LOT-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/lots"));
        }
    }

    @Nested
    @DisplayName("GET /lots/{lotNumber}/edit - ロットマスタ編集画面")
    class EditLotMaster {

        @Test
        @DisplayName("ロットマスタ編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            LotMaster lot = createTestLotMaster("LOT-001", "ITEM-001");
            Mockito.when(lotMasterUseCase.getLotMaster("LOT-001"))
                .thenReturn(Optional.of(lot));

            mockMvc.perform(MockMvcRequestBuilders.get("/lots/LOT-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("lots/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("lotTypes"));
        }

        @Test
        @DisplayName("ロットマスタが見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(lotMasterUseCase.getLotMaster("LOT-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/lots/LOT-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/lots"));
        }
    }

    @Nested
    @DisplayName("POST /lots/{lotNumber} - ロットマスタ更新処理")
    class UpdateLotMaster {

        @Test
        @DisplayName("ロットマスタを更新できる")
        void shouldUpdateLotMaster() throws Exception {
            LotMaster updated = createTestLotMaster("LOT-001", "ITEM-001");
            Mockito.when(lotMasterUseCase.updateLotMaster(
                    ArgumentMatchers.eq("LOT-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/lots/LOT-001")
                    .param("lotNumber", "LOT-001")
                    .param("itemCode", "ITEM-001")
                    .param("lotType", "PURCHASED")
                    .param("manufactureDate", "2025-01-01")
                    .param("quantity", "150")
                    .param("version", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/lots"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /lots/{lotNumber}/delete - ロットマスタ削除処理")
    class DeleteLotMaster {

        @Test
        @DisplayName("ロットマスタを削除できる")
        void shouldDeleteLotMaster() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/lots/LOT-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/lots"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(lotMasterUseCase).deleteLotMaster("LOT-001");
        }
    }

    private LotMaster createTestLotMaster(String lotNumber, String itemCode) {
        return LotMaster.builder()
            .id(1)
            .lotNumber(lotNumber)
            .itemCode(itemCode)
            .lotType(LotType.PURCHASED)
            .manufactureDate(LocalDate.of(2025, 1, 1))
            .expirationDate(LocalDate.of(2026, 1, 1))
            .quantity(new BigDecimal("100"))
            .warehouseCode("WH-001")
            .version(1)
            .build();
    }
}
