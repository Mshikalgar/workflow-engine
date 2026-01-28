package com.mansur.workflow.workflow_engine.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createRequest_withoutAuth_shouldReturn403() throws Exception {
        mockMvc.perform(
                post("/requests?type=LEAVE&createdBy=mansur")
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "mansur", roles = "REQUESTER")
    void createRequest_withAuth_shouldReturn200() throws Exception {
        mockMvc.perform(
                post("/requests?type=LEAVE&createdBy=mansur")
        ).andExpect(status().isOk());
    }
}


