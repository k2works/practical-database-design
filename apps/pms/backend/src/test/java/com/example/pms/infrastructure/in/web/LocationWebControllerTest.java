package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.LocationUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.location.Location;
import com.example.pms.domain.model.location.LocationType;
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

import java.util.List;

/**
 * 場所マスタ画面コントローラーテスト.
 */
@WebMvcTest(LocationWebController.class)
@DisplayName("場所マスタ画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class LocationWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocationUseCase locationUseCase;

    @Nested
    @DisplayName("GET /locations - 場所一覧")
    class ListLocations {

        @Test
        @DisplayName("場所一覧画面を表示できる")
        void shouldDisplayLocationList() throws Exception {
            Location location = createTestLocation("LOC-001", "第一倉庫");
            PageResult<Location> pageResult = new PageResult<>(List.of(location), 0, 20, 1);
            Mockito.when(locationUseCase.getLocations(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/locations"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("locations/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("locations"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Location location = createTestLocation("LOC-001", "第一倉庫");
            PageResult<Location> pageResult = new PageResult<>(List.of(location), 0, 20, 1);
            Mockito.when(locationUseCase.getLocations(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("倉庫")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/locations")
                    .param("keyword", "倉庫"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("locations/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "倉庫"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Location location = createTestLocation("LOC-001", "第一倉庫");
            PageResult<Location> pageResult = new PageResult<>(List.of(location), 1, 10, 25);
            Mockito.when(locationUseCase.getLocations(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/locations")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /locations/new - 場所登録画面")
    class NewLocation {

        @Test
        @DisplayName("場所登録画面を表示できる")
        void shouldDisplayNewForm() throws Exception {
            Mockito.when(locationUseCase.getAllLocations()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.get("/locations/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("locations/new"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("locationTypes"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("parentLocations"));
        }
    }

    @Nested
    @DisplayName("POST /locations - 場所登録処理")
    class CreateLocation {

        @Test
        @DisplayName("場所を登録できる")
        void shouldCreateLocation() throws Exception {
            Location created = createTestLocation("NEW-001", "新規場所");
            Mockito.when(locationUseCase.createLocation(ArgumentMatchers.any())).thenReturn(created);

            mockMvc.perform(MockMvcRequestBuilders.post("/locations")
                    .param("locationCode", "NEW-001")
                    .param("locationName", "新規場所")
                    .param("locationType", "WAREHOUSE"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/locations"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
        }

        @Test
        @DisplayName("バリデーションエラー時は入力画面に戻る")
        void shouldReturnFormOnValidationError() throws Exception {
            Mockito.when(locationUseCase.getAllLocations()).thenReturn(List.of());

            mockMvc.perform(MockMvcRequestBuilders.post("/locations")
                    .param("locationCode", "")
                    .param("locationName", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("locations/new"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("form", "locationCode", "locationName"));
        }
    }

    private Location createTestLocation(String code, String name) {
        return Location.builder()
            .locationCode(code)
            .locationName(name)
            .locationType(LocationType.WAREHOUSE)
            .build();
    }
}
