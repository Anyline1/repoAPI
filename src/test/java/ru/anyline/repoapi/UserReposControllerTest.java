package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import ru.anyline.repoapi.controller.UserReposController;
import ru.anyline.repoapi.model.UserRepos;

import java.util.List;

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

    @Test
    void getUserRepos_when4xxClientError_shouldSetErrorMessage() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        ResponseEntity<UserRepos[]> mockResponseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenReturn(mockResponseEntity);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("error", "Error 404: User not found or rate limit exceeded.");
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_when5xxServerError_shouldSetErrorMessage() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        ResponseEntity<UserRepos[]> mockResponseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenReturn(mockResponseEntity);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("error", "Error 404: User not found or rate limit exceeded.");
        verifyNoMoreInteractions(model);
    }
}
