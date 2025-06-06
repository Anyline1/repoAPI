package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.anyline.repoapi.controller.UserReposController;
import ru.anyline.repoapi.model.UserRepos;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserReposControllerTest {

    private RestTemplate restTemplate;

    private final Model model;

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
        verify(model).addAttribute("error", "Error 404: User not found.");
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_whenHttpClientErrorException_shouldSetClientErrorMessage() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        HttpClientErrorException mockException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenThrow(mockException);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("error", "Client error: 400 BAD_REQUEST");
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_whenHttpServerErrorException_shouldSetServerErrorMessage() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        HttpServerErrorException mockException = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenThrow(mockException);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("error", "Server error: 500 INTERNAL_SERVER_ERROR - Internal Server Error");
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_whenResourceAccessException_shouldSetErrorMessage() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        ResourceAccessException mockException = new ResourceAccessException("Unable to connect to the server");

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenThrow(mockException);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("error", "Resource access error: Unable to connect to the server. Please try again later.");
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_whenApiReturnsEmptyArray_shouldNotAddReposAttribute() {
        String username = "tU";
        String url = "http://localhost:8080/repos/" + username;
        UserRepos[] emptyReposArray = new UserRepos[0];
        ResponseEntity<UserRepos[]> mockResponseEntity = new ResponseEntity<>(emptyReposArray, HttpStatus.OK);

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenReturn(mockResponseEntity);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("username", username);
        verify(model, never()).addAttribute(eq("repos"), any());
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_whenMaxNumberOfReposReturned_shouldAddAllReposToModel() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        UserRepos[] maxReposArray = new UserRepos[100]; 
        for (int i = 0; i < 100; i++) {
            maxReposArray[i] = new UserRepos();
        }
        ResponseEntity<UserRepos[]> mockResponseEntity = new ResponseEntity<>(maxReposArray, HttpStatus.OK);

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenReturn(mockResponseEntity);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("repos", List.of(maxReposArray));
        verify(model).addAttribute("username", username);
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_whenResponseBodyIsNull_shouldNotAddReposAttribute() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        ResponseEntity<UserRepos[]> mockResponseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenReturn(mockResponseEntity);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("username", username);
        verify(model, never()).addAttribute(eq("repos"), any());
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_whenUsernameProvidedButNoReposFound_shouldVerifyModelAttributes() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        UserRepos[] emptyReposArray = new UserRepos[0];
        ResponseEntity<UserRepos[]> mockResponseEntity = new ResponseEntity<>(emptyReposArray, HttpStatus.OK);

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenReturn(mockResponseEntity);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("username", username);
        verify(model, never()).addAttribute(eq("repos"), any());
        verify(model, never()).addAttribute(eq("error"), any());
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_whenNonOkStatusCodeNot4xxOr5xx_shouldReturnReposView() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        ResponseEntity<UserRepos[]> mockResponseEntity = new ResponseEntity<>(HttpStatus.MULTIPLE_CHOICES);

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenReturn(mockResponseEntity);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("username", username);
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_whenUsernameContainsSpecialCharacters_shouldEncodeUrlCorrectly() {
        String username = "test@user#123";
        String encodedUsername = "test%40user%23123";
        String url = "http://localhost:8080/repos/" + encodedUsername;
        UserRepos[] mockReposArray = new UserRepos[]{new UserRepos(), new UserRepos()};
        ResponseEntity<UserRepos[]> mockResponseEntity = new ResponseEntity<>(mockReposArray, HttpStatus.OK);

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenReturn(mockResponseEntity);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("repos", List.of(mockReposArray));
        verify(model).addAttribute("username", username);
        verifyNoMoreInteractions(model);
        verify(restTemplate).getForEntity(url, UserRepos[].class);
    }

    @Test
    void getUserRepos_whenResponseStatusCodeIsNull_shouldSetErrorMessage() {
        String username = "testUser";
        String url = "http://localhost:8080/repos/" + username;
        ResponseEntity<UserRepos[]> mockResponseEntity = mock(ResponseEntity.class);

        when(restTemplate.getForEntity(url, UserRepos[].class)).thenReturn(mockResponseEntity);
        when(mockResponseEntity.getStatusCode()).thenReturn(null);

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("error", "Error 404: User not found.");
        verifyNoMoreInteractions(model);
    }

    @Test
    void getUserRepos_whenReposFoundButUsernameIsEmpty_shouldVerifyModelAttributes() {
        String username = "";

        String result = userReposController.getUserRepos(username, model);

        assertEquals("repos", result);
        verify(model).addAttribute("error", "Username is required.");
        verifyNoMoreInteractions(model);
        verifyNoInteractions(restTemplate);
    }

}
