package ru.anyline.repoapi;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import ru.anyline.repoapi.controller.GitHubController;
import ru.anyline.repoapi.model.UserRepos;
import ru.anyline.repoapi.service.GitHubServiceImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    public void getAllRepos_whenNoReposAreCached_shouldReturnEmptyList() {
        List<UserRepos> expectedRepos = Collections.emptyList();
        when(gitHubServiceImpl.getCachedRepos()).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getAllRepos();

        assertEquals(expectedRepos, actualResponse.getBody());
    }

    @Test
    public void getAllRepos_whenSomeReposAreCached_shouldReturnAListOfRepos() {
        List<UserRepos> expectedRepos = new ArrayList<>();
        expectedRepos.add(new UserRepos(1L, "testUser1", "repo1", "https://github.com/testUser1/repo1"));
        expectedRepos.add(new UserRepos(2L, "testUser2", "repo2", "https://github.com/testUser2/repo2"));
        when(gitHubServiceImpl.getCachedRepos()).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getAllRepos();

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedRepos, actualResponse.getBody());
    }

    @Test
    public void getAllRepos_whenGitHubAPIEncountersAnInternalServerError_shouldReturnInternalServerError() {
        when(gitHubServiceImpl.getCachedRepos()).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getAllRepos();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
    }

    @Test
    public void getAllRepos_whenUserHasMoreThan100Repositories_shouldHandlePagination() {
        int expectedReposCount = 150;
        List<UserRepos> expectedRepos = new ArrayList<>();
        for (int i = 0; i < expectedReposCount; i++) {
            expectedRepos.add(new UserRepos(i + 1L, "testUser", "repo" + (i + 1), "https://github.com/testUser/repo" + (i + 1)));
        }
        when(gitHubServiceImpl.getCachedRepos()).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getAllRepos();

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedReposCount, actualResponse.getBody().size());
    }

    @Test
    public void getReposByUsername_whenUserHasNoPublicRepos_shouldReturnEmptyList() {
        String username = "testUser";
        List<UserRepos> expectedRepos = Collections.emptyList();
        when(gitHubServiceImpl.getReposByUsername(username)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        assertEquals(expectedRepos, actualResponse.getBody());
    }

    @Test
    public void getReposByUsername_whenSomeReposAreCached_shouldReturnAListOfRepos() {
        String validUsername = "testUser";
        List<UserRepos> expectedRepos = new ArrayList<>();
        expectedRepos.add(new UserRepos(1L, validUsername, "repo1", "https://github.com/testUser/repo1"));
        expectedRepos.add(new UserRepos(2L, validUsername, "repo2", "https://github.com/testUser/repo2"));
        when(gitHubServiceImpl.getReposByUsername(validUsername)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(validUsername);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedRepos, actualResponse.getBody());
    }

    @Test
    public void getReposByUsername_whenInvalidUsernameProvided_shouldReturnError() {
        String invalidUsername = "";
        ResponseEntity<List<UserRepos>> expectedResponse = ResponseEntity.badRequest().build();

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(invalidUsername);

        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }
    
    @Test
    public void getReposByUsername_whenGitHubServiceImplThrowsRuntimeException_shouldReturnInternalServerError() {
        String username = "testUser";
        when(gitHubServiceImpl.getReposByUsername(username)).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
    }

    @Test
    public void getReposByUsername_whenUsernameIsNull_shouldReturnBadRequest() {
        String username = null;
        ResponseEntity<List<UserRepos>> expectedResponse = ResponseEntity.badRequest().build();

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }
    
    @Test
    public void getReposByUsername_whenGitHubServiceImplReturnsNull_shouldReturnOkWithEmptyList() {
        String username = "testUser";
        when(gitHubServiceImpl.getReposByUsername(username)).thenReturn(null);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertTrue(actualResponse.getBody().isEmpty());
    }

    @Test
    public void getReposByUsername_shouldPassCorrectUsernameToGitHubServiceImpl() {
        String username = "testUser";
        List<UserRepos> expectedRepos = new ArrayList<>();
        expectedRepos.add(new UserRepos(1L, username, "repo1", "https://github.com/testUser/repo1"));
        when(gitHubServiceImpl.getReposByUsername(username)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        verify(gitHubServiceImpl).getReposByUsername(username);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedRepos, actualResponse.getBody());
    }
    
    @Test
    public void getReposByUsername_whenUsernameIsEmptyString_shouldReturnBadRequest() {
        String emptyUsername = "";
        ResponseEntity<List<UserRepos>> expectedResponse = ResponseEntity.badRequest().build();

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(emptyUsername);

        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());
    }

    @Test
    public void getReposByUsername_whenGitHubServiceImplReturnsEmptyList_shouldReturnOkWithEmptyList() {
        String username = "testUser";
        List<UserRepos> expectedRepos = Collections.emptyList();
        when(gitHubServiceImpl.getReposByUsername(username)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertTrue(actualResponse.getBody().isEmpty());
        verify(gitHubServiceImpl).getReposByUsername(username);
    }
    
    @Test
    public void getReposByUsername_whenValidResponseReturned_shouldHaveNonNullBody() {
        String validUsername = "testUser";
        List<UserRepos> expectedRepos = new ArrayList<>();
        expectedRepos.add(new UserRepos(1L, validUsername, "repo1", "https://github.com/testUser/repo1"));
        when(gitHubServiceImpl.getReposByUsername(validUsername)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(validUsername);
    
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertEquals(expectedRepos, actualResponse.getBody());
    }
    
    @Test
    public void getReposByUsername_whenUsernameContainsSpecialCharacters_shouldReturnOk() {
        String usernameWithSpecialChars = "test-user@123";
        List<UserRepos> expectedRepos = new ArrayList<>();
        expectedRepos.add(new UserRepos(1L, usernameWithSpecialChars, "repo1", "https://github.com/test-user@123/repo1"));
        when(gitHubServiceImpl.getReposByUsername(usernameWithSpecialChars)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(usernameWithSpecialChars);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertEquals(expectedRepos, actualResponse.getBody());
        verify(gitHubServiceImpl).getReposByUsername(usernameWithSpecialChars);
    }
    
    @Test
    public void getReposByUsername_whenUsernameIsVeryLong_shouldReturnOk() {
        String longUsername = "a".repeat(100);
        List<UserRepos> expectedRepos = new ArrayList<>();
        expectedRepos.add(new UserRepos(1L, longUsername, "repo1", "https://github.com/" + longUsername + "/repo1"));
        when(gitHubServiceImpl.getReposByUsername(longUsername)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(longUsername);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertEquals(expectedRepos, actualResponse.getBody());
        verify(gitHubServiceImpl).getReposByUsername(longUsername);
    }
    
    @Test
    public void getReposByUsername_shouldHaveCorrectAnnotations() throws NoSuchMethodException {
        Method method = GitHubController.class.getMethod("getReposByUsername", String.class);

        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        assertNotNull(getMapping);
        assertArrayEquals(new String[]{"/cached/{username}"}, getMapping.value());

        Tag tag = method.getAnnotation(Tag.class);
        assertNotNull(tag);
        assertEquals("Get repos from DB by username", tag.name());
        assertEquals("Выводит JSON список репозиториев пользователя из БД", tag.description());
    }

    @Test
    public void getReposByUsername_whenGitHubServiceImplThrowsSpecificException_shouldReturnInternalServerError() {
        String username = "testUser";
        when(gitHubServiceImpl.getReposByUsername(username)).thenThrow(new IllegalStateException("Specific exception"));
    
        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());
        verify(gitHubServiceImpl).getReposByUsername(username);
    }
    
    @Test
    public void getReposByUsername_shouldSetCorrectResponseHeaders() {
        String username = "testUser";
        List<UserRepos> expectedRepos = new ArrayList<>();
        expectedRepos.add(new UserRepos(1L, username, "repo1", "https://github.com/testUser/repo1"));
        when(gitHubServiceImpl.getReposByUsername(username)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertTrue(actualResponse.getHeaders().containsKey("Content-Type"));
        assertEquals("application/json", actualResponse.getHeaders().getFirst("Content-Type"));
        assertNotNull(actualResponse.getHeaders().getFirst("Date"));
    }
    
    @Test
    public void getReposByUsername_whenLargeNumberOfRepositories_shouldPerformEfficiently() {
        String username = "testUser";
        int largeRepoCount = 1000;
        List<UserRepos> expectedRepos = new ArrayList<>();
        for (int i = 0; i < largeRepoCount; i++) {
            expectedRepos.add(new UserRepos((long) i, username, "repo" + i, "https://github.com/" + username + "/repo" + i));
        }
        when(gitHubServiceImpl.getReposByUsername(username)).thenReturn(expectedRepos);

        long startTime = System.currentTimeMillis();
        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);
        long endTime = System.currentTimeMillis();

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertEquals(largeRepoCount, actualResponse.getBody().size());
        assertTrue((endTime - startTime) < 1000, "Response time should be less than 1 second");
    }
    

    @Test
    public void getReposByUsername_whenGitHubServiceImplThrowsNullPointerException_shouldReturnInternalServerError() {
        String username = "testUser";
        when(gitHubServiceImpl.getReposByUsername(username)).thenThrow(new NullPointerException("Specific exception"));

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());
        verify(gitHubServiceImpl).getReposByUsername(username);
    }
    
    @Test
    public void getReposByUsername_whenGitHubServiceImplReturnsListWithNullElements_shouldReturnOkWithFilteredList() {
        String username = "testUser";
        List<UserRepos> reposWithNulls = new ArrayList<>();
        reposWithNulls.add(new UserRepos(1L, username, "repo1", "https://github.com/testUser/repo1"));
        reposWithNulls.add(null);
        reposWithNulls.add(new UserRepos(2L, username, "repo2", "https://github.com/testUser/repo2"));
        when(gitHubServiceImpl.getReposByUsername(username)).thenReturn(reposWithNulls);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertEquals(2, actualResponse.getBody().size());
        assertFalse(actualResponse.getBody().contains(null));
        verify(gitHubServiceImpl).getReposByUsername(username);
    }
    
    @Test
    public void getReposByUsername_shouldNotModifyResponseBody_whenGitHubServiceImplReturnsValidList() {
        String username = "testUser";
        List<UserRepos> expectedRepos = new ArrayList<>();
        expectedRepos.add(new UserRepos(1L, username, "repo1", "https://github.com/testUser/repo1"));
        expectedRepos.add(new UserRepos(2L, username, "repo2", "https://github.com/testUser/repo2"));
        when(gitHubServiceImpl.getReposByUsername(username)).thenReturn(expectedRepos);

        ResponseEntity<List<UserRepos>> actualResponse = gitHubController.getReposByUsername(username);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertNotNull(actualResponse.getBody());
        assertEquals(expectedRepos, actualResponse.getBody());
        verify(gitHubServiceImpl).getReposByUsername(username);
    }
    
    @Test
    public void getAllRepos_shouldHandleConcurrentRequestsWithoutDataRaceConditions() throws InterruptedException {
        int numberOfThreads = 10;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        List<ResponseEntity<List<UserRepos>>> responses = Collections.synchronizedList(new ArrayList<>());

        List<UserRepos> expectedRepos = Arrays.asList(
            new UserRepos(1L, "user1", "repo1", "https://github.com/user1/repo1"),
            new UserRepos(2L, "user2", "repo2", "https://github.com/user2/repo2")
        );
        when(gitHubServiceImpl.getCachedRepos()).thenReturn(expectedRepos);

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(() -> {
                responses.add(gitHubController.getAllRepos());
                latch.countDown();
            }).start();
        }

        latch.await(5, TimeUnit.SECONDS);

        assertEquals(numberOfThreads, responses.size());
        for (ResponseEntity<List<UserRepos>> response : responses) {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedRepos, response.getBody());
        }

        verify(gitHubServiceImpl, times(numberOfThreads)).getCachedRepos();
    }

}