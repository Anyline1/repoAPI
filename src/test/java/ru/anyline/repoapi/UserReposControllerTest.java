package ru.anyline.repoapi;

import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import ru.anyline.repoapi.controller.UserReposController;

class UserReposControllerTest {

    private RestTemplate restTemplate;

    private Model model;

    private UserReposController userReposController;


    @Test
    void getUserRepos_whenUsernameIsNull_shouldReturnReposView() {
        // Arrange
        String username = null;

        // Act
        String result = userReposController.getUserRepos(username, model);

        // Assert
        assertEquals("repos", result);
        verify(model).addAttribute("error", "Username is required to fetch repositories.");
        verifyNoMoreInteractions(model);
        verifyNoInteractions(restTemplate);
    }
}
