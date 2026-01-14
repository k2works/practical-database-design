package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.LocationUseCase;
import com.example.pms.application.port.in.MpsUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.plan.MasterProductionSchedule;
import com.example.pms.domain.model.plan.PlanStatus;
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
 * 基準生産計画画面コントローラーテスト.
 */
@WebMvcTest(MpsWebController.class)
@DisplayName("基準生産計画画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class MpsWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MpsUseCase mpsUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private LocationUseCase locationUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
        Mockito.when(locationUseCase.getAllLocations()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /mps - 基準生産計画一覧")
    class ListMps {

        @Test
        @DisplayName("基準生産計画一覧画面を表示できる")
        void shouldDisplayMpsList() throws Exception {
            MasterProductionSchedule mps = createTestMps("MPS-001", "ITEM-001");
            PageResult<MasterProductionSchedule> pageResult = new PageResult<>(List.of(mps), 0, 20, 1);
            Mockito.when(mpsUseCase.getMpsList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/mps"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mps/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("mpsList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("ステータスでフィルタできる")
        void shouldFilterByStatus() throws Exception {
            MasterProductionSchedule mps = createTestMps("MPS-001", "ITEM-001");
            PageResult<MasterProductionSchedule> pageResult = new PageResult<>(List.of(mps), 0, 20, 1);
            Mockito.when(mpsUseCase.getMpsList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq(PlanStatus.DRAFT),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/mps")
                    .param("status", "DRAFT"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mps/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("status", "DRAFT"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            MasterProductionSchedule mps = createTestMps("MPS-001", "ITEM-001");
            PageResult<MasterProductionSchedule> pageResult = new PageResult<>(List.of(mps), 0, 20, 1);
            Mockito.when(mpsUseCase.getMpsList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.eq("ITEM")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/mps")
                    .param("keyword", "ITEM"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mps/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "ITEM"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            MasterProductionSchedule mps = createTestMps("MPS-001", "ITEM-001");
            PageResult<MasterProductionSchedule> pageResult = new PageResult<>(List.of(mps), 1, 10, 25);
            Mockito.when(mpsUseCase.getMpsList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull(),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/mps")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /mps/new - 基準生産計画登録画面")
    class NewMps {

        @Test
        @DisplayName("基準生産計画登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/mps/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mps/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /mps - 基準生産計画登録処理")
    class CreateMps {

        @Test
        @DisplayName("基準生産計画を登録できる")
        void shouldCreateMps() throws Exception {
            MasterProductionSchedule created = createTestMps("MPS-001", "ITEM-001");
            Mockito.when(mpsUseCase.createMps(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/mps")
                    .param("itemCode", "ITEM-001")
                    .param("planDate", "2024-01-01")
                    .param("planQuantity", "100")
                    .param("dueDate", "2024-01-15"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/mps"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/mps")
                    .param("itemCode", "")
                    .param("planQuantity", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mps/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "itemCode"));
        }
    }

    @Nested
    @DisplayName("GET /mps/{mpsNumber} - 基準生産計画詳細画面")
    class ShowMps {

        @Test
        @DisplayName("基準生産計画詳細画面を表示できる")
        void shouldDisplayMpsDetail() throws Exception {
            MasterProductionSchedule mps = createTestMps("MPS-001", "ITEM-001");
            Mockito.when(mpsUseCase.getMpsWithOrders("MPS-001"))
                .thenReturn(Optional.of(mps));

            mockMvc.perform(MockMvcRequestBuilders.get("/mps/MPS-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mps/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("mps"));
        }

        @Test
        @DisplayName("基準生産計画が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(mpsUseCase.getMpsWithOrders("MPS-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/mps/MPS-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/mps"));
        }
    }

    @Nested
    @DisplayName("GET /mps/{mpsNumber}/edit - 基準生産計画編集画面")
    class EditMps {

        @Test
        @DisplayName("基準生産計画編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            MasterProductionSchedule mps = createTestMps("MPS-001", "ITEM-001");
            Mockito.when(mpsUseCase.getMps("MPS-001"))
                .thenReturn(Optional.of(mps));

            mockMvc.perform(MockMvcRequestBuilders.get("/mps/MPS-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("mps/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }

        @Test
        @DisplayName("基準生産計画が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(mpsUseCase.getMps("MPS-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/mps/MPS-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/mps"));
        }
    }

    @Nested
    @DisplayName("POST /mps/{mpsNumber} - 基準生産計画更新処理")
    class UpdateMps {

        @Test
        @DisplayName("基準生産計画を更新できる")
        void shouldUpdateMps() throws Exception {
            MasterProductionSchedule updated = createTestMps("MPS-001", "ITEM-001");
            Mockito.when(mpsUseCase.updateMps(
                    ArgumentMatchers.eq("MPS-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/mps/MPS-001")
                    .param("itemCode", "ITEM-001")
                    .param("planDate", "2024-01-01")
                    .param("planQuantity", "150")
                    .param("dueDate", "2024-01-20"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/mps"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /mps/{mpsNumber}/confirm - 基準生産計画確定処理")
    class ConfirmMps {

        @Test
        @DisplayName("基準生産計画を確定できる")
        void shouldConfirmMps() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/mps/MPS-001/confirm"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/mps"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(mpsUseCase).confirmMps("MPS-001");
        }
    }

    @Nested
    @DisplayName("POST /mps/{mpsNumber}/cancel - 基準生産計画取消処理")
    class CancelMps {

        @Test
        @DisplayName("基準生産計画を取消できる")
        void shouldCancelMps() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/mps/MPS-001/cancel"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/mps"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(mpsUseCase).cancelMps("MPS-001");
        }
    }

    private MasterProductionSchedule createTestMps(String mpsNumber, String itemCode) {
        return MasterProductionSchedule.builder()
            .id(1)
            .mpsNumber(mpsNumber)
            .itemCode(itemCode)
            .planDate(LocalDate.of(2024, 1, 1))
            .planQuantity(new BigDecimal("100"))
            .dueDate(LocalDate.of(2024, 1, 15))
            .status(PlanStatus.DRAFT)
            .locationCode("LOC-001")
            .remarks("テスト用")
            .version(1)
            .build();
    }
}
