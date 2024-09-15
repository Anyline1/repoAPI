package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.anyline.repoapi.controller.GitHubController;
import ru.anyline.repoapi.service.GitHubServiceImpl;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(GitHubController.class)
public class MockTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubServiceImpl gitHubServiceImpl;

    @Test
    @WithMockUser(
            username = "user",
            password = "password",
            roles = "user"
    )
    public void testShowRepos() throws Exception{
        when(gitHubServiceImpl.getRepositories("testuser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/repos/testuser"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }
    @Test
    public void testAccessDeniedForUnauthenticatedUsers() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is4xxClientError());

    }


}
