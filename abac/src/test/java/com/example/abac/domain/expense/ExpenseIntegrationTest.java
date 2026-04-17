package com.example.abac.domain.expense;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Expense RBAC + ABAC Integration")
class ExpenseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("인증 없이 /expenses 조회 -> 401")
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/expenses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("EMPLOYEE 본인 지출 작성 -> 201, DRAFT")
    void employee_create() throws Exception {
        String emp1 = login("emp1");
        mockMvc.perform(bearer(post("/expenses"), emp1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":"50000","description":"lunch"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.ownerId").value(1))
                .andExpect(jsonPath("$.departmentId").value(1));
    }

    @Test
    @DisplayName("EMPLOYEE 데이터 스코프 — 본인 건만 조회")
    void employee_dataScope_onlyOwn() throws Exception {
        String emp1 = login("emp1");
        String emp2 = login("emp2");
        createExpense(emp1, "10000", "emp1 own");
        createExpense(emp2, "20000", "emp2 own");

        mockMvc.perform(bearer(get("/expenses"), emp1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("MANAGER 데이터 스코프 — 자기 부서 건만 조회")
    void manager_dataScope_onlyOwnDepartment() throws Exception {
        String emp1 = login("emp1");   // dept 1
        String emp2 = login("emp2");   // dept 2
        String mgr1 = login("mgr1");   // dept 1
        createExpense(emp1, "10000", "dept1 expense");
        createExpense(emp2, "20000", "dept2 expense");

        mockMvc.perform(bearer(get("/expenses"), mgr1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].departmentId").value(1));
    }

    @Test
    @DisplayName("ADMIN 데이터 스코프 — 전체 조회")
    void admin_dataScope_all() throws Exception {
        String emp1 = login("emp1");
        String emp2 = login("emp2");
        String admin1 = login("admin1");
        createExpense(emp1, "10000", "a");
        createExpense(emp2, "20000", "b");

        mockMvc.perform(bearer(get("/expenses"), admin1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("EMPLOYEE가 타인 지출 단건조회 -> 404 (Specification이 숨김)")
    void employee_crossOwner_getById_404() throws Exception {
        String emp1 = login("emp1");
        String emp2 = login("emp2");
        long otherId = createExpense(emp2, "20000", "emp2 secret");

        mockMvc.perform(bearer(get("/expenses/" + otherId), emp1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("EMPLOYEE가 타인 지출 PATCH -> 403 (ExpensePolicy 거부)")
    void employee_crossOwner_patch_403() throws Exception {
        String emp1 = login("emp1");
        String emp2 = login("emp2");
        long otherId = createExpense(emp2, "20000", "emp2 secret");

        mockMvc.perform(bearer(patch("/expenses/" + otherId), emp1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":"1","description":"hacked"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("MANAGER 타 부서 승인 시도 -> 403")
    void manager_crossDepartment_approve_403() throws Exception {
        String emp2 = login("emp2");   // dept 2
        String mgr1 = login("mgr1");   // dept 1
        long id = createExpense(emp2, "20000", "dept2");
        mockMvc.perform(bearer(post("/expenses/" + id + "/submit"), emp2)).andExpect(status().isOk());

        mockMvc.perform(bearer(post("/expenses/" + id + "/approve"), mgr1))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("MANAGER 한도 초과 건 승인 시도 -> 403, ADMIN은 승인 가능")
    void manager_overLimit_approve_403() throws Exception {
        String emp1 = login("emp1");
        String mgr1 = login("mgr1");
        String admin1 = login("admin1");
        long id = createExpense(emp1, "1500000", "big");
        mockMvc.perform(bearer(post("/expenses/" + id + "/submit"), emp1)).andExpect(status().isOk());

        mockMvc.perform(bearer(post("/expenses/" + id + "/approve"), mgr1))
                .andExpect(status().isForbidden());

        mockMvc.perform(bearer(post("/expenses/" + id + "/approve"), admin1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("해피 패스: emp1 submit -> mgr1 approve -> fin1 pay")
    void happyPath_submit_approve_pay() throws Exception {
        String emp1 = login("emp1");
        String mgr1 = login("mgr1");
        String fin1 = login("fin1");
        long id = createExpense(emp1, "50000", "snack");

        mockMvc.perform(bearer(post("/expenses/" + id + "/submit"), emp1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));

        mockMvc.perform(bearer(post("/expenses/" + id + "/approve"), mgr1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(bearer(post("/expenses/" + id + "/pay"), fin1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    @DisplayName("FINANCE가 SUBMITTED 상태 지급 시도 -> 403 (canPay는 APPROVED만 허용)")
    void finance_paySubmitted_403() throws Exception {
        String emp1 = login("emp1");
        String fin1 = login("fin1");
        long id = createExpense(emp1, "50000", "snack");
        mockMvc.perform(bearer(post("/expenses/" + id + "/submit"), emp1)).andExpect(status().isOk());

        mockMvc.perform(bearer(post("/expenses/" + id + "/pay"), fin1))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("이미 APPROVED 된 건 다시 approve -> 403")
    void doubleApprove_403() throws Exception {
        String emp1 = login("emp1");
        String mgr1 = login("mgr1");
        long id = createExpense(emp1, "50000", "snack");
        mockMvc.perform(bearer(post("/expenses/" + id + "/submit"), emp1)).andExpect(status().isOk());
        mockMvc.perform(bearer(post("/expenses/" + id + "/approve"), mgr1)).andExpect(status().isOk());

        mockMvc.perform(bearer(post("/expenses/" + id + "/approve"), mgr1))
                .andExpect(status().isForbidden());
    }

    // --- helpers ---

    private String login(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = json.get("token").asText();
        assertThat(token).isNotBlank();
        return token;
    }

    private long createExpense(String token, String amount, String description) throws Exception {
        MvcResult result = mockMvc.perform(bearer(post("/expenses"), token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"" + amount + "\",\"description\":\"" + description + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }

    private static MockHttpServletRequestBuilder bearer(MockHttpServletRequestBuilder builder, String token) {
        return builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
