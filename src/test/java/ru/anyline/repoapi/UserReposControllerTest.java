package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import ru.anyline.repoapi.controller.UserReposController;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserReposControllerTest {

    private RestTemplate restTemplate;

    private Model model;

    private UserReposController userReposController;


    @Test
    void getUserRepos_whenUsernameIsNull_shouldReturnReposView() {
        String username = null;

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("error", "Username is required to fetch repositories.");
        verifyNoMoreInteractions(model);
        verifyNoInteractions(restTemplate);
    }
}
