package ru.anyline.repoapi;

import ru.anyline.repoapi.controller.GitHubController;
import ru.anyline.repoapi.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GitHubController.class)
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubService gitHubService;

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void testIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void testGetRepos() throws Exception {
        mockMvc.perform(post("/repos").param("username", "Anyline1"))
                .andExpect(status().isOk())
                .andExpect(view().name("repos"))
                .andExpect(model().attributeExists("username"))
                .andExpect(model().attributeExists("reposJson"));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void testShowRepos() throws Exception {
        when(gitHubService.getRepositories("testuser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/repos/testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("repos"))
                .andExpect(model().attributeExists("username"))
                .andExpect(model().attributeExists("reposJson"));
    }

    @Test
    @WithMockUser(username = "user", password = "password", roles = "USER")
    public void testLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
