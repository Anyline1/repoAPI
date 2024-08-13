package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.anyline.repoapi.controller.GitHubController;
import ru.anyline.repoapi.service.GitHubService;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(GitHubController.class)
public class MockTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubService gitHubService;

    @Test
    @WithMockUser(
            username = "user",
            password = "password",
            roles = "user"
    )
    public void testShowRepos() throws Exception{
        when(gitHubService.getRepositories("testuser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/repos/testuser"))
                .andExpect(status().isOk());
    }
    @Test
    public void testAccessDeniedForUnauthenticatedUsers() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is4xxClientError());

    }


}
