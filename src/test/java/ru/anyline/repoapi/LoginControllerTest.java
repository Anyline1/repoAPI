package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.anyline.repoapi.controller.LoginController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(LoginController.class)
class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnLoginPageWhenGetRequestIsMadeToLoginEndpoint() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
    
    @Test
    void shouldReturnMethodNotAllowedWhenPostRequestIsMadeToLoginEndpoint() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().isMethodNotAllowed());
    }
}
