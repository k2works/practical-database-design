package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.IssueUseCase;
import com.example.pms.domain.model.common.PageResult;
import com.example.pms.domain.model.inventory.Issue;
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
 * 払出履歴画面コントローラーテスト.
 */
@WebMvcTest(IssueWebController.class)
@DisplayName("払出履歴画面コントローラーテスト")
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class IssueWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IssueUseCase issueUseCase;

    @Nested
    @DisplayName("GET /issues - 払出履歴一覧")
    class ListIssues {

        @Test
        @DisplayName("払出履歴一覧画面を表示できる")
        void shouldDisplayIssueList() throws Exception {
            Issue issue = createTestIssue("ISS-001", "WO-001");
            PageResult<Issue> pageResult = new PageResult<>(List.of(issue), 0, 20, 1);
            Mockito.when(issueUseCase.getIssueList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/issues"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("issues/list"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("issueList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentPage"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("totalPages"));
        }

        @Test
        @DisplayName("キーワードで検索できる")
        void shouldSearchByKeyword() throws Exception {
            Issue issue = createTestIssue("ISS-001", "WO-001");
            PageResult<Issue> pageResult = new PageResult<>(List.of(issue), 0, 20, 1);
            Mockito.when(issueUseCase.getIssueList(
                    ArgumentMatchers.eq(0),
                    ArgumentMatchers.eq(20),
                    ArgumentMatchers.eq("WO-001")))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/issues")
                    .param("keyword", "WO-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("issues/list"))
                .andExpect(MockMvcResultMatchers.model().attribute("keyword", "WO-001"));
        }

        @Test
        @DisplayName("ページネーションパラメータを指定できる")
        void shouldAcceptPaginationParams() throws Exception {
            Issue issue = createTestIssue("ISS-001", "WO-001");
            PageResult<Issue> pageResult = new PageResult<>(List.of(issue), 1, 10, 25);
            Mockito.when(issueUseCase.getIssueList(
                    ArgumentMatchers.eq(1),
                    ArgumentMatchers.eq(10),
                    ArgumentMatchers.isNull()))
                .thenReturn(pageResult);

            mockMvc.perform(MockMvcRequestBuilders.get("/issues")
                    .param("page", "1")
                    .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("currentPage", 1))
                .andExpect(MockMvcResultMatchers.model().attribute("totalPages", 3));
        }
    }

    @Nested
    @DisplayName("GET /issues/{issueNumber} - 払出詳細画面")
    class ShowIssue {

        @Test
        @DisplayName("払出詳細画面を表示できる")
        void shouldDisplayIssueDetail() throws Exception {
            Issue issue = createTestIssue("ISS-001", "WO-001");
            Mockito.when(issueUseCase.getIssue("ISS-001"))
                .thenReturn(Optional.of(issue));

            mockMvc.perform(MockMvcRequestBuilders.get("/issues/ISS-001"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("issues/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("issue"));
        }

        @Test
        @DisplayName("払出が見つからない場合は一覧にリダイレクト")
        void shouldRedirectWhenNotFound() throws Exception {
            Mockito.when(issueUseCase.getIssue("ISS-999"))
                .thenReturn(Optional.empty());

            mockMvc.perform(MockMvcRequestBuilders.get("/issues/ISS-999"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/issues"));
        }
    }

    private Issue createTestIssue(String issueNumber, String workOrderNumber) {
        return Issue.builder()
            .id(1)
            .issueNumber(issueNumber)
            .workOrderNumber(workOrderNumber)
            .routingSequence(1)
            .locationCode("LOC-001")
            .issueDate(LocalDate.of(2024, 1, 20))
            .issuerCode("EMP-001")
            .build();
    }
}
