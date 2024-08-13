package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.anyline.repoapi.controller.GitHubController;
import ru.anyline.repoapi.service.GitHubService;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(GitHubController.class)
public class MockTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubService gitHubService;

    @Test
    public void testShowRepos() throws Exception{
        when(gitHubService.getRepositories("testuser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/repos/testuser"))
                .andExpect(status().isOk());
    }


}
