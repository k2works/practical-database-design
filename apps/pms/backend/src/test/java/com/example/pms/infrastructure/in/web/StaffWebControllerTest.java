package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.staff.Staff;
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
import java.util.Optional;

/**
 * 担当者マスタ画面コントローラーテスト.
 */
@WebMvcTest(StaffWebController.class)
@DisplayName("担当者マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class StaffWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StaffUseCase staffUseCase;

    @Nested
    @DisplayName("GET /staff - 担当者一覧")
    class ListStaff {

        @Test
        @DisplayName("担当者一覧画面を表示できる")
        void shouldDisplayStaffList() throws Exception {
            Staff staff = createTestStaff("STAFF-001", "山田太郎");
            PageResult<Staff> pageResult = new PageResult<>(List.of(staff), 0, 20, 1);
            Mockito.when(staffUseCase.getStaffList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/staff"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("staffList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Staff staff = createTestStaff("STAFF-001", "山田太郎");
            PageResult<Staff> pageResult = new PageResult<>(List.of(staff), 0, 20, 1);
            Mockito.when(staffUseCase.getStaffList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("山田")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/staff")
                    .param("keyword", "山田"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "山田"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Staff staff = createTestStaff("STAFF-001", "山田太郎");
            PageResult<Staff> pageResult = new PageResult<>(List.of(staff), 1, 10, 25);
            Mockito.when(staffUseCase.getStaffList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/staff")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /staff/new - 担当者登録画面")
    class NewStaff {

        @Test
        @DisplayName("担当者登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/staff/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }
    }

    @Nested
    @DisplayName("POST /staff - 担当者登録処理")
    class CreateStaff {

        @Test
        @DisplayName("担当者を登録できる")
        void shouldCreateStaff() throws Exception {
            Staff created = createTestStaff("NEW-001", "新規担当者");
            Mockito.when(staffUseCase.createStaff(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/staff")
                    .param("staffCode", "NEW-001")
                    .param("staffName", "新規担当者")
                    .param("effectiveFrom", "2024-01-01"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/staff")
                    .param("staffCode", "")
                    .param("staffName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "staffCode", "staffName"));
        }
    }

    @Nested
    @DisplayName("GET /staff/{staffCode}/{effectiveFrom}/edit - 担当者編集画面")
    class EditStaff {

        @Test
        @DisplayName("担当者編集画面を表示できる")
        void shouldDisplayEditForm() throws Exception {
            Staff staff = createTestStaff("STAFF-001", "山田太郎");
            Mockito.when(staffUseCase.getStaff("STAFF-001", LocalDate.of(2024, 1, 1)))
                .thenReturn(Optional.of(staff));

            mockMvc.perform(MockMvcRequestBuilders.get("/staff/STAFF-001/2024-01-01/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("staff/edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"));
        }

        @Test
        @DisplayName("担当者が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(staffUseCase.getStaff("STAFF-999", LocalDate.of(2024, 1, 1)))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/staff/STAFF-999/2024-01-01/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff"));
        }
    }

    @Nested
    @DisplayName("POST /staff/{staffCode}/{effectiveFrom} - 担当者更新処理")
    class UpdateStaff {

        @Test
        @DisplayName("担当者を更新できる")
        void shouldUpdateStaff() throws Exception {
            Staff updated = createTestStaff("STAFF-001", "更新後担当者");
            Mockito.when(staffUseCase.updateStaff(
                    ArgumentMatchers.eq("STAFF-001"),
                    ArgumentMatchers.eq(LocalDate.of(2024, 1, 1)),
                    ArgumentMatchers.any()))
                .thenReturn(updated);

            mockMvc.perform(MockMvcRequestBuilders.post("/staff/STAFF-001/2024-01-01")
                    .param("staffCode", "STAFF-001")
                    .param("staffName", "更新後担当者")
                    .param("effectiveFrom", "2024-01-01"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }
    }

    @Nested
    @DisplayName("POST /staff/{staffCode}/{effectiveFrom}/delete - 担当者削除処理")
    class DeleteStaff {

        @Test
        @DisplayName("担当者を削除できる")
        void shouldDeleteStaff() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/staff/STAFF-001/2024-01-01/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/staff"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));

            Mockito.verify(staffUseCase).deleteStaff("STAFF-001", LocalDate.of(2024, 1, 1));
        }
    }

    private Staff createTestStaff(String code, String name) {
        return Staff.builder()
            .staffCode(code)
            .staffName(name)
            .effectiveFrom(LocalDate.of(2024, 1, 1))
            .effectiveTo(LocalDate.of(9999, 12, 31))
            .departmentCode("DEPT-001")
            .email("test@example.com")
            .phoneNumber("03-1234-5678")
            .build();
    }
}
