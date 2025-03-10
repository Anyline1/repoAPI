package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import ru.anyline.repoapi.controller.UserReposController;
import ru.anyline.repoapi.model.UserRepos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

    @Test
    void getUserRepos_whenUsernameIsEmpty_shouldReturnReposView() {
        String username = "";

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("error", "Username is required.");
        verifyNoMoreInteractions(model);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getUserRepos_whenSuccessfulApiResponse_shouldPopulateModelWithRepos() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        UserRepos[] mockReposArray = new UserRepos[]{new UserRepos(), new UserRepos()};
        ResponseEntity<UserRepos[]> mockResponseEntity = new ResponseEntity<>(mockReposArray, HttpStatus.OK);

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenReturn(mockResponseEntity);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("repos", List.of(mockReposArray));
        verify(model).addAttribute("username", username);
        verifyNoMoreInteractions(model);
    }

}
