package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.anyline.repoapi.controller.LoginController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.fail;
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

    @Test
    void shouldVerifyLoginEndpointCachingBehavior() throws Exception {
        mockMvc.perform(get("/login")
                        .header("If-Modified-Since", "Wed, 21 Oct 2015 07:28:00 GMT"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(header().string("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(header().string("Expires", "0"));
    }

    @Test
    void shouldHandleConcurrentRequestsToLoginEndpointCorrectly() throws Exception {
        int concurrentRequests = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentRequests);
        CountDownLatch latch = new CountDownLatch(concurrentRequests);

        for (int i = 0; i < concurrentRequests; i++) {
            executorService.submit(() -> {
                try {
                    mockMvc.perform(get("/login"))
                            .andExpect(status().isOk())
                            .andExpect(view().name("login"))
                            .andExpect(content().contentType("text/html;charset=UTF-8"));
                } catch (Exception e) {
                    fail("Exception occurred during concurrent request: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executorService.shutdown();
    }

    @Test
    void shouldRedirectToReposWhenPostRequestIsMadeToLoginEndpoint() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/repos"));
    }

    @Test
    void shouldReturnStatusCode302ForPostRequestToLogin() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/repos"));
    }

    @Test
    void shouldIncludeLocationHeaderWithValueReposInResponse() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/repos"));
    }

    @Test
    void shouldVerifyNoBodyContentForPostRequestToLogin() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/repos"))
                .andExpect(content().string(""));
    }

    @Test
    void shouldHandlePostRequestsWithDifferentContentTypesCorrectly() throws Exception {
        String[] contentTypes = {
                "application/x-www-form-urlencoded",
                "application/json",
                "multipart/form-data",
                "text/plain"
        };

        for (String contentType : contentTypes) {
            mockMvc.perform(post("/login")
                            .contentType(contentType))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/repos"));
        }
    }
}
