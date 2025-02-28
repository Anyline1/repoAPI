package ru.anyline.repoapi;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.anyline.repoapi.controller.LoginController;

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
}
