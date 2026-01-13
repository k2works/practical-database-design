package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.DepartmentUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.department.Department;
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

import java.time.LocalDate;
import java.util.List;

/**
 * 部門マスタ画面コントローラーテスト.
 */
@WebMvcTest(DepartmentWebController.class)
@DisplayName("部門マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class DepartmentWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentUseCase departmentUseCase;

    @Nested
    @DisplayName("GET /departments - 部門一覧")
    class ListDepartments {

        @Test
        @DisplayName("部門一覧画面を表示できる")
        void shouldDisplayDepartmentList() throws Exception {
            Department department = createTestDepartment("DEPT-001", "製造部");
            PageResult<Department> pageResult = new PageResult<>(List.of(department), 0, 20, 1);
            Mockito.when(departmentUseCase.getDepartments(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/departments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("departments"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Department department = createTestDepartment("DEPT-001", "製造部");
            PageResult<Department> pageResult = new PageResult<>(List.of(department), 0, 20, 1);
            Mockito.when(departmentUseCase.getDepartments(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("製造")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/departments")
                    .param("keyword", "製造"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "製造"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Department department = createTestDepartment("DEPT-001", "製造部");
            PageResult<Department> pageResult = new PageResult<>(List.of(department), 1, 10, 25);
            Mockito.when(departmentUseCase.getDepartments(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/departments")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /departments/new - 部門登録画面")
    class NewDepartment {

        @Test
        @DisplayName("部門登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/departments/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /departments - 部門登録処理")
    class CreateDepartment {

        @Test
        @DisplayName("部門を登録できる")
        void shouldCreateDepartment() throws Exception {
            Department created = createTestDepartment("NEW-001", "新規部門");
            Mockito.when(departmentUseCase.createDepartment(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/departments")
                    .param("departmentCode", "NEW-001")
                    .param("departmentName", "新規部門")
                    .param("validFrom", "2024-01-01"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/departments"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/departments")
                    .param("departmentCode", "")
                    .param("departmentName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "departmentCode", "departmentName"));
        }
    }

    private Department createTestDepartment(String code, String name) {
        return Department.builder()
            .departmentCode(code)
            .departmentName(name)
            .departmentPath("/" + code)
            .lowestLevel(true)
            .validFrom(LocalDate.of(2024, 1, 1))
            .validTo(LocalDate.of(9999, 12, 31))
            .build();
    }
}
