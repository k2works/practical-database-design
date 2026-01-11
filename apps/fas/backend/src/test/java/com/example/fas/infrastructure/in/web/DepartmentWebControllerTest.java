package com.example.fas.infrastructure.in.web;

import com.example.fas.application.port.in.DepartmentUseCase;
import com.example.fas.application.port.in.dto.DepartmentResponse;
import com.example.fas.domain.model.common.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
    @DisplayName("GET /departments")
    class ListDepartments {

        @Test
        @DisplayName("部門一覧画面を表示できる")
        void shouldDisplayDepartmentList() throws Exception {
            DepartmentResponse response = createTestDepartment("10000", "営業本部", 1);
            PageResult<DepartmentResponse> pageResult = new PageResult<>(List.of(response), 0, 20, 1);
            Mockito.when(departmentUseCase.getDepartments(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/departments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("departments"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"));
        }

        @Test
        @DisplayName("組織階層でフィルタできる")
        void shouldFilterByLevel() throws Exception {
            DepartmentResponse response = createTestDepartment("10000", "営業本部", 1);
            PageResult<DepartmentResponse> pageResult = new PageResult<>(List.of(response), 0, 20, 1);
            Mockito.when(departmentUseCase.getDepartments(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.eq(1)
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/departments")
                    .param("level", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("selectedLevel", 1));
        }

        @Test
        @DisplayName("キーワードでフィルタできる")
        void shouldFilterByKeyword() throws Exception {
            DepartmentResponse response = createTestDepartment("10000", "営業本部", 1);
            PageResult<DepartmentResponse> pageResult = new PageResult<>(List.of(response), 0, 20, 1);
            Mockito.when(departmentUseCase.getDepartments(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.eq("営業"),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/departments")
                    .param("keyword", "営業"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "営業"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            DepartmentResponse response = createTestDepartment("10000", "営業本部", 1);
            PageResult<DepartmentResponse> pageResult = new PageResult<>(List.of(response), 1, 10, 15);
            Mockito.when(departmentUseCase.getDepartments(
                ArgumentMatchers.eq(1),
                ArgumentMatchers.eq(10),
                ArgumentMatchers.isNull(),
                ArgumentMatchers.isNull()
            )).thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/departments")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("currentSize", 10));
        }
    }

    @Nested
    @DisplayName("GET /departments/{departmentCode}")
    class ShowDepartment {

        @Test
        @DisplayName("部門詳細画面を表示できる")
        void shouldDisplayDepartmentDetail() throws Exception {
            DepartmentResponse response = createTestDepartment("10000", "営業本部", 1);
            Mockito.when(departmentUseCase.getDepartment("10000")).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.get("/departments/10000"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("department"));
        }
    }

    @Nested
    @DisplayName("GET /departments/new")
    class NewDepartmentForm {

        @Test
        @DisplayName("部門登録フォームを表示できる")
        void shouldDisplayNewDepartmentForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/departments/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /departments")
    class CreateDepartment {

        @Test
        @DisplayName("部門を登録できる")
        void shouldCreateDepartment() throws Exception {
            DepartmentResponse created = createTestDepartment("99999", "テスト部門", 2);
            Mockito.when(departmentUseCase.createDepartment(ArgumentMatchers.any()))
                .thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/departments")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("departmentCode", "99999")
                    .param("departmentName", "テスト部門")
                    .param("departmentShortName", "テスト")
                    .param("organizationLevel", "2")
                    .param("departmentPath", "00000~10000~99999")
                    .param("lowestLevelFlag", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/departments"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は登録フォームに戻る")
        void shouldReturnToFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/departments")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("departmentCode", "")
                    .param("departmentName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/new"))
                .andExpect(MockMvcResultMatchers.model().hasErrors());
        }
    }

    @Nested
    @DisplayName("GET /departments/{departmentCode}/edit")
    class EditDepartmentForm {

        @Test
        @DisplayName("部門編集フォームを表示できる")
        void shouldDisplayEditDepartmentForm() throws Exception {
            DepartmentResponse response = createTestDepartment("10000", "営業本部", 1);
            Mockito.when(departmentUseCase.getDepartment("10000")).thenReturn(response);

            mockMvc.perform(MockMvcRequestBuilders.get("/departments/10000/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("departments/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /departments/{departmentCode}")
    class UpdateDepartment {

        @Test
        @DisplayName("部門を更新できる")
        void shouldUpdateDepartment() throws Exception {
            DepartmentResponse updated = createTestDepartment("10000", "更新後営業本部", 1);
            Mockito.when(departmentUseCase.updateDepartment(
                ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/departments/10000")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("departmentCode", "10000")
                    .param("departmentName", "更新後営業本部")
                    .param("departmentShortName", "営業")
                    .param("organizationLevel", "1")
                    .param("departmentPath", "00000~10000")
                    .param("lowestLevelFlag", "0"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/departments/10000"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /departments/{departmentCode}/delete")
    class DeleteDepartment {

        @Test
        @DisplayName("部門を削除できる")
        void shouldDeleteDepartment() throws Exception {
            Mockito.doNothing().when(departmentUseCase).deleteDepartment("99999");

            mockMvc.perform(MockMvcRequestBuilders.post("/departments/99999/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/departments"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    private DepartmentResponse createTestDepartment(String code, String name, int level) {
        return DepartmentResponse.builder()
            .departmentCode(code)
            .departmentName(name)
            .departmentShortName(name.length() > 10 ? name.substring(0, 10) : name)
            .organizationLevel(level)
            .departmentPath("00000~" + code)
            .lowestLevel(false)
            .organizationLevelName(getOrganizationLevelName(level))
            .build();
    }

    private String getOrganizationLevelName(int level) {
        return switch (level) {
            case 0 -> "全社";
            case 1 -> "本部";
            case 2 -> "部";
            case 3 -> "課";
            default -> "その他";
        };
    }
}
