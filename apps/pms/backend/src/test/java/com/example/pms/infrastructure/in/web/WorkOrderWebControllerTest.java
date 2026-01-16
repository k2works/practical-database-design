package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.LocationUseCase;
import com.example.pms.application.port.in.OrderUseCase;
import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.application.port.in.command.CreateWorkOrderCommand;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.process.WorkOrder;
import com.example.pms.domain.model.process.WorkOrderStatus;
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
 * 作業指示画面コントローラーテスト.
 */
@WebMvcTest(WorkOrderWebController.class)
@DisplayName("作業指示画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class WorkOrderWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkOrderUseCase workOrderUseCase;

    @MockitoBean
    private OrderUseCase orderUseCase;

    @MockitoBean
    private ItemUseCase itemUseCase;

    @MockitoBean
    private StaffUseCase staffUseCase;

    @MockitoBean
    private LocationUseCase locationUseCase;

    @BeforeEach
    void setUp() {
        Mockito.when(orderUseCase.getAllOrders()).thenReturn(Collections.emptyList());
        Mockito.when(itemUseCase.getAllItems()).thenReturn(Collections.emptyList());
        Mockito.when(staffUseCase.getAllStaff()).thenReturn(Collections.emptyList());
        Mockito.when(locationUseCase.getAllLocations()).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("GET /work-orders - 作業指示一覧")
    class ListWorkOrders {

        @Test
        @DisplayName("作業指示一覧画面を表示できる")
        void shouldDisplayWorkOrdersList() throws Exception {
            WorkOrder workOrder = createTestWorkOrder("WO-001", "ORD-001");
            PageResult<WorkOrder> pageResult = new PageResult<>(List.of(workOrder), 0, 20, 1);
            Mockito.when(workOrderUseCase.getWorkOrderList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/work-orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("work-orders/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("workOrderList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            WorkOrder workOrder = createTestWorkOrder("WO-001", "ORD-001");
            PageResult<WorkOrder> pageResult = new PageResult<>(List.of(workOrder), 0, 20, 1);
            Mockito.when(workOrderUseCase.getWorkOrderList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("ORD-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/work-orders")
                    .param("keyword", "ORD-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("work-orders/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "ORD-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            WorkOrder workOrder = createTestWorkOrder("WO-001", "ORD-001");
            PageResult<WorkOrder> pageResult = new PageResult<>(List.of(workOrder), 1, 10, 25);
            Mockito.when(workOrderUseCase.getWorkOrderList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/work-orders")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /work-orders/new - 作業指示登録画面")
    class NewWorkOrder {

        @Test
        @DisplayName("作業指示登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/work-orders/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("work-orders/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /work-orders - 作業指示登録処理")
    class CreateWorkOrder {

        @Test
        @DisplayName("作業指示を登録できる")
        void shouldCreateWorkOrder() throws Exception {
            WorkOrder created = createTestWorkOrder("WO-001", "ORD-001");
            Mockito.when(workOrderUseCase.createWorkOrder(ArgumentMatchers.any(CreateWorkOrderCommand.class))).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/work-orders")
                    .param("orderNumber", "ORD-001")
                    .param("workOrderDate", "2024-01-20")
                    .param("itemCode", "ITEM-001")
                    .param("orderQuantity", "100")
                    .param("locationCode", "LOC-001")
                    .param("plannedStartDate", "2024-01-21")
                    .param("plannedEndDate", "2024-01-25")
                    .param("status", "NOT_STARTED"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/work-orders"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/work-orders")
                    .param("orderNumber", "")
                    .param("orderQuantity", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("work-orders/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "orderNumber"));
        }
    }

    @Nested
    @DisplayName("GET /work-orders/{workOrderNumber} - 作業指示詳細画面")
    class ShowWorkOrder {

        @Test
        @DisplayName("作業指示詳細画面を表示できる")
        void shouldDisplayWorkOrderDetail() throws Exception {
            WorkOrder workOrder = createTestWorkOrder("WO-001", "ORD-001");
            Mockito.when(workOrderUseCase.getWorkOrder("WO-001"))
                .thenReturn(Optional.of(workOrder));

            mockMvc.perform(MockMvcRequestBuilders.get("/work-orders/WO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("work-orders/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("workOrder"));
        }

        @Test
        @DisplayName("作業指示が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(workOrderUseCase.getWorkOrder("WO-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/work-orders/WO-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/work-orders"));
        }
    }

    @Nested
    @DisplayName("GET /work-orders/{workOrderNumber}/edit - 作業指示編集画面")
    class EditWorkOrder {

        @Test
        @DisplayName("作業指示編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            WorkOrder workOrder = createTestWorkOrder("WO-001", "ORD-001");
            Mockito.when(workOrderUseCase.getWorkOrder("WO-001"))
                .thenReturn(Optional.of(workOrder));

            mockMvc.perform(MockMvcRequestBuilders.get("/work-orders/WO-001/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("work-orders/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }

        @Test
        @DisplayName("作業指示が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(workOrderUseCase.getWorkOrder("WO-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/work-orders/WO-999/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/work-orders"));
        }
    }

    @Nested
    @DisplayName("POST /work-orders/{workOrderNumber} - 作業指示更新処理")
    class UpdateWorkOrder {

        @Test
        @DisplayName("作業指示を更新できる")
        void shouldUpdateWorkOrder() throws Exception {
            WorkOrder updated = createTestWorkOrder("WO-001", "ORD-001");
            Mockito.when(workOrderUseCase.updateWorkOrder(
                    ArgumentMatchers.eq("WO-001"),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/work-orders/WO-001")
                    .param("orderNumber", "ORD-001")
                    .param("workOrderDate", "2024-01-20")
                    .param("itemCode", "ITEM-001")
                    .param("orderQuantity", "150")
                    .param("locationCode", "LOC-001")
                    .param("plannedStartDate", "2024-01-21")
                    .param("plannedEndDate", "2024-01-25")
                    .param("status", "IN_PROGRESS"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/work-orders"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /work-orders/{workOrderNumber}/delete - 作業指示削除処理")
    class DeleteWorkOrder {

        @Test
        @DisplayName("作業指示を削除できる")
        void shouldDeleteWorkOrder() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/work-orders/WO-001/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/work-orders"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(workOrderUseCase).deleteWorkOrder("WO-001");
        }
    }

    private WorkOrder createTestWorkOrder(String workOrderNumber, String orderNumber) {
        return WorkOrder.builder()
            .id(1)
            .workOrderNumber(workOrderNumber)
            .orderNumber(orderNumber)
            .workOrderDate(LocalDate.of(2024, 1, 20))
            .itemCode("ITEM-001")
            .orderQuantity(new BigDecimal("100"))
            .locationCode("LOC-001")
            .plannedStartDate(LocalDate.of(2024, 1, 21))
            .plannedEndDate(LocalDate.of(2024, 1, 25))
            .status(WorkOrderStatus.NOT_STARTED)
            .completedFlag(false)
            .remarks("テスト用")
            .version(1)
            .build();
    }
}
