package ru.anyline.repoapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.anyline.repoapi.controller.GitHubController;
import ru.anyline.repoapi.model.UserRepos;
import ru.anyline.repoapi.service.GitHubServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class GitHubControllerTest {

    @Mock
    private GitHubServiceImpl gitHubServiceImpl;

    @InjectMocks
    private GitHubController gitHubController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getRepositories_whenUserHasNoPublicRepos_shouldReturnEmptyList() {
        String username = "testUser";
        List<UserRepos> expectedRepos = Collections.emptyList();
        when(gitHubServiceImpl.getRepositories(username)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getRepositories(username);

        assertEquals(expectedRepos, actualResponse.getBody());
    }

    @Test
    public void getRepositories_whenUsernameIsNotProvided_shouldReturnError() {
        String username = "";
        ResponseEntity<List<UserRepos>> expectedResponse = ResponseEntity.badRequest().build();

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getRepositories(username);

        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    public void getRepositories_whenUsernameIsInvalid_shouldReturnBadRequest() {
        String invalidUsername = "";
        when(gitHubServiceImpl.getRepositories(invalidUsername)).thenThrow(new IllegalArgumentException("Invalid username"));

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getRepositories(invalidUsername);

        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
    }

    @Test
    public void getRepositories_whenValidUsernameProvided_shouldReturnResponseWithValidFormat() {
        String validUsername = "testUser";
        List<UserRepos> expectedRepos = new ArrayList<>();
        expectedRepos.add(new UserRepos(1L, validUsername, "repo1", "https://github.com/testUser/repo1"));
        when(gitHubServiceImpl.getRepositories(validUsername)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getRepositories(validUsername);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedRepos, actualResponse.getBody());
    }

    @Test
    public void getRepositories_whenRateLimitExceeded_shouldReturnTooManyRequests() {
        String username = "testUser";
        when(gitHubServiceImpl.getRepositories(username)).thenThrow(new RuntimeException("Rate limit exceeded"));

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getRepositories(username);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, actualResponse.getStatusCode());
    }

    @Test
    public void getRepositories_whenGitHubAPIReturnsALargeNumberOfRepositories_shouldReturnResponseWithValidFormat() {
        String validUsername = "testUser";
        int expectedReposCount = 100;
        List<UserRepos> expectedRepos = new ArrayList<>();
        for (int i = 0; i < expectedReposCount; i++) {
            expectedRepos.add(new UserRepos(i + 1L, validUsername, "repo" + (i + 1), "https://github.com/testUser/repo" + (i + 1)));
        }
        when(gitHubServiceImpl.getRepositories(validUsername)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getRepositories(validUsername);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedReposCount, actualResponse.getBody().size());
    }

    @Test
    public void getCustomRepository_whenUsernameIsNull_shouldReturnBadRequest() {
        String username = null;
        String repoName = "testRepo";
        ResponseEntity<UserRepos> expectedResponse = ResponseEntity.badRequest().build();

        ResponseEntity<UserRepos> actualResponse = gitHubController.getCustomRepository(username, repoName);

        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    public void getCustomRepository_whenUsernameIsEmpty_shouldReturnBadRequest() {
        String username = "";
        String repoName = "testRepo";
        ResponseEntity<UserRepos> expectedResponse = ResponseEntity.badRequest().build();

        ResponseEntity<UserRepos> actualResponse = gitHubController.getCustomRepository(username, repoName);

        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    public void getCustomRepository_whenRepoNameIsNull_shouldReturnBadRequest() {
        String username = "testUser";
        String repoName = null;
        ResponseEntity<UserRepos> expectedResponse = ResponseEntity.badRequest().build();

        ResponseEntity<UserRepos> actualResponse = gitHubController.getCustomRepository(username, repoName);

        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    public void getCustomRepository_whenRepoNameIsEmpty_shouldReturnBadRequest() {
        String username = "testUser";
        String repoName = "";
        ResponseEntity<UserRepos> expectedResponse = ResponseEntity.badRequest().build();

        ResponseEntity<UserRepos> actualResponse = gitHubController.getCustomRepository(username, repoName);

        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    public void getCustomRepository_whenValidUsernameAndRepoNameProvided_shouldReturnStatusOK() {
        String validUsername = "testUser";
        String validRepoName = "testRepo";
        UserRepos expectedRepo = new UserRepos(1L, validUsername, validRepoName, "https://github.com/testUser/testRepo");
        when(gitHubServiceImpl.getRepository(validUsername, validRepoName)).thenReturn(expectedRepo);

        ResponseEntity<UserRepos> actualResponse = gitHubController.getCustomRepository(validUsername, validRepoName);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedRepo, actualResponse.getBody());
    }

    @Test
    public void getCustomRepository_whenValidUsernameAndRepoNameProvided_shouldReturnStatusOKRegardlessOfRepoPrivacy() {
        String validUsername = "testUser";
        String validRepoName = "testRepo";
        UserRepos expectedRepo = new UserRepos(1L, validUsername, validRepoName, "https://github.com/testUser/testRepo");
        when(gitHubServiceImpl.getRepository(validUsername, validRepoName)).thenReturn(expectedRepo);

        ResponseEntity<UserRepos> actualResponse = gitHubController.getCustomRepository(validUsername, validRepoName);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedRepo, actualResponse.getBody());
    }


}