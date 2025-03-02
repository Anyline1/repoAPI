package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.anyline.repoapi.controller.LoginController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
    
    @Test
    void shouldReturnHtmlContentTypeWhenGetRequestIsMadeToLoginEndpoint() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionOccurs() throws Exception {
        LoginController loginControllerMock = Mockito.mock(LoginController.class);
        Mockito.when(loginControllerMock.login()).thenThrow(new RuntimeException("Simulated internal error"));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(loginControllerMock).build();

        mockMvc.perform(get("/login"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Internal Server Error")));
    }

    @Test
    void shouldHandleRequestsWithDifferentAcceptHeadersCorrectly() throws Exception {
        mockMvc.perform(get("/login").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));

        mockMvc.perform(get("/login").accept("text/html"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));

        mockMvc.perform(get("/login").accept("*/*"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void shouldHandleLoginEndpointWhenAccessedThroughReverseProxy() throws Exception {
        mockMvc.perform(get("/login")
                        .header("X-Forwarded-Proto", "https")
                        .header("X-Forwarded-Host", "example.com")
                        .header("X-Forwarded-For", "203.0.113.195"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(header().string("X-Frame-Options", "DENY"));
    }

    @Test
    void shouldMaintainConsistentBehaviorWhenAccessedFromDifferentIPAddresses() throws Exception {
        String[] ipAddresses = {"192.168.1.1", "10.0.0.1", "172.16.0.1", "8.8.8.8"};

        for (String ipAddress : ipAddresses) {
            mockMvc.perform(get("/login")
                            .header("X-Forwarded-For", ipAddress))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login"))
                    .andExpect(content().contentType("text/html;charset=UTF-8"));
        }
    }


    @Test
    void shouldHandleRequestsWithMalformedURLsOrSpecialCharacters() throws Exception {
        String[] malformedPaths = {
                "/login%20",
                "/login?param=value",
                "/login#fragment",
                "/login/extra",
                "/login/../login",
                "/login/./",
                "/login//",
                "/login%2F",
                "/login%00"
        };

        for (String path : malformedPaths) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login"))
                    .andExpect(content().contentType("text/html;charset=UTF-8"));
        }
    }
}
