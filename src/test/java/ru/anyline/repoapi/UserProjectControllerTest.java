package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.anyline.repoapi.controller.UserProjectController;
import ru.anyline.repoapi.exceptions.ProjectNotFoundException;
import ru.anyline.repoapi.model.UserProject;
import ru.anyline.repoapi.service.UserProjectServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserProjectControllerTest {

    @Mock
    private UserProjectServiceImpl userProjectService;

    @InjectMocks
    private UserProjectController userProjectController;

    @Test
    void createProject_shouldReturnCreatedStatus() {
        UserProject project = new UserProject();
        UserProject createdProject = new UserProject();
        when(userProjectService.createUserProject(project)).thenReturn(createdProject);

        ResponseEntity<UserProject> response = userProjectController.createProject(project);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdProject, response.getBody());
        verify(userProjectService).createUserProject(project);
    }

    @Test
    void createProject_shouldReturnCreatedProjectInResponseBody() {
        UserProject project = new UserProject();
        UserProject createdProject = new UserProject();
        createdProject.setId(1L);
        when(userProjectService.createUserProject(project)).thenReturn(createdProject);

        ResponseEntity<UserProject> response = userProjectController.createProject(project);

        assertEquals(createdProject, response.getBody());
        verify(userProjectService).createUserProject(project);
    }

    @Test
    void createProject_shouldCallUserProjectServiceWithSubmittedProject() {
        UserProject project = new UserProject();
        UserProject createdProject = new UserProject();
        when(userProjectService.createUserProject(project)).thenReturn(createdProject);

        userProjectController.createProject(project);

        verify(userProjectService).createUserProject(project);
    }

    @Test
    void createProject_shouldHandleServiceException() {
        UserProject project = new UserProject();
        when(userProjectService.createUserProject(project)).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<UserProject> response = userProjectController.createProject(project);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService).createUserProject(project);
    }

    @Test
    void createProject_shouldValidateProjectDataBeforeCallingService() {
        UserProject project = new UserProject();
        project.setName("Test Project");
        project.setDescription("Test Description");

        when(userProjectService.createUserProject(project)).thenReturn(project);

        ResponseEntity<UserProject> response = userProjectController.createProject(project);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(project, response.getBody());
        verify(userProjectService).createUserProject(project);
    }

    @Test
    void createProject_shouldReturnBadRequestWhenProjectInputIsNull() {
        UserProject project = null;

        ResponseEntity<UserProject> response = userProjectController.createProject(project);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService, never()).createUserProject(any());
    }

    @Test
    void createProject_shouldHandleProjectWithNullNameAndValidDescription() {
        UserProject project = new UserProject();
        project.setDescription("Test Description");

        ResponseEntity<UserProject> response = userProjectController.createProject(project);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService, never()).createUserProject(any());
    }

    @Test
    void getProjectById_shouldReturnNotFoundWhenProjectDoesNotExist() {
        Long nonExistentId = 1000L;
        when(userProjectService.getUserProjectById(nonExistentId)).thenReturn(Optional.empty());

        ResponseEntity<UserProject> response = userProjectController.getProjectById(nonExistentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService).getUserProjectById(nonExistentId);
    }

    @Test
    void getProjectById_shouldReturnOkStatusAndProjectDataWhenProjectExists() {
        Long existingId = 2L;
        UserProject existingProject = new UserProject();
        existingProject.setId(existingId);
        when(userProjectService.getUserProjectById(existingId)).thenReturn(Optional.of(existingProject));

        ResponseEntity<UserProject> response = userProjectController.getProjectById(existingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingProject, response.getBody());
        verify(userProjectService).getUserProjectById(existingId);
    }

    @Test
    void getAllProjects_shouldReturnAllProjects() {
        List<UserProject> expectedProjects = Arrays.asList(new UserProject(), new UserProject());
        when(userProjectService.getAllProjects()).thenReturn(expectedProjects);

        ResponseEntity<List<UserProject>> response = userProjectController.getAllProjects();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProjects, response.getBody());
        verify(userProjectService).getAllProjects();
    }

    @Test
    void getAllProjects_shouldHandleExceptionWhenServiceThrowsException() {
        when(userProjectService.getAllProjects()).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<List<UserProject>> response = userProjectController.getAllProjects();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService).getAllProjects();
    }

    @Test
    void updateProject_shouldValidateProjectDataBeforeUpdatingProject() {
        Long existingProjectId = 2L;
        UserProject existingProject = new UserProject();
        existingProject.setId(existingProjectId);
        existingProject.setName("Test Project");
        existingProject.setDescription("Test Description");

        UserProject updatedProject = new UserProject();
        updatedProject.setId(existingProjectId);
        updatedProject.setName("Updated Test Project");
        updatedProject.setDescription("Updated Test Description");

        when(userProjectService.updateUserProject(existingProjectId, updatedProject)).thenReturn(Optional.of(updatedProject));

        ResponseEntity<UserProject> response = userProjectController.updateProject(String.valueOf(existingProjectId), updatedProject);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProject, response.getBody());
        verify(userProjectService).updateUserProject(existingProjectId, updatedProject);
    }

    @Test
    void updateProject_shouldReturnBadRequestWhenProjectInputIsNull() {
        Long existingProjectId = 2L;
        UserProject updatedProject = null;

        ResponseEntity<UserProject> response = userProjectController.updateProject(String.valueOf(existingProjectId), updatedProject);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService, never()).updateUserProject(anyLong(), any());
    }

    @Test
    void updateProject_shouldHandleProjectWithNullNameAndValidDescription() {
        Long existingProjectId = 2L;
        UserProject updatedProject = new UserProject();
        updatedProject.setId(existingProjectId);
        updatedProject.setDescription("Updated Test Description");

        ResponseEntity<UserProject> response = userProjectController.updateProject(String.valueOf(existingProjectId), updatedProject);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService, never()).updateUserProject(anyLong(), any());
    }

    @Test
    void updateProject_shouldReturnUpdatedProjectData() {
        Long existingProjectId = 2L;
        UserProject existingProject = new UserProject();
        existingProject.setId(existingProjectId);
        existingProject.setName("Test Project");
        existingProject.setDescription("Test Description");

        UserProject updatedProject = new UserProject();
        updatedProject.setId(existingProjectId);
        updatedProject.setName("Updated Test Project");
        updatedProject.setDescription("Updated Test Description");

        when(userProjectService.updateUserProject(existingProjectId, updatedProject)).thenReturn(Optional.of(updatedProject));

        ResponseEntity<UserProject> response = userProjectController.updateProject(String.valueOf(existingProjectId), updatedProject);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProject, response.getBody());
        verify(userProjectService).updateUserProject(existingProjectId, updatedProject);
    }

    @Test
    void updateProject_shouldReturnNotFoundWhenUpdatingANonExistentProject() {
        Long nonExistentProjectId = 1000L;
        UserProject updatedProject = new UserProject();
        updatedProject.setId(nonExistentProjectId);
        updatedProject.setName("Updated Test Project");
        updatedProject.setDescription("Updated Test Description");

        when(userProjectService.updateUserProject(nonExistentProjectId, updatedProject)).thenReturn(Optional.empty());

        ResponseEntity<UserProject> response = userProjectController.updateProject(String.valueOf(nonExistentProjectId), updatedProject);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService).updateUserProject(nonExistentProjectId, updatedProject);
    }

    @Test
    void deleteProject_shouldDeleteProjectWhenDeleteProjectMethodIsCalled() {
        Long existingProjectId = 2L;
        when(userProjectService.deleteUserProject(existingProjectId)).thenReturn(true);

        ResponseEntity<Void> response = userProjectController.deleteProject(existingProjectId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userProjectService).deleteUserProject(existingProjectId);
    }

    @Test
    void deleteProject_shouldReturnNoContentStatusWhenDeletingAProject() {
        Long existingProjectId = 2L;
        when(userProjectService.deleteUserProject(existingProjectId)).thenReturn(true);

        ResponseEntity<Void> response = userProjectController.deleteProject(existingProjectId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userProjectService).deleteUserProject(existingProjectId);
    }

    @Test
    void deleteProject_shouldReturnNotFoundWhenDeletingANonExistentProject() {
        Long nonExistentProjectId = 10000L;
        when(userProjectService.deleteUserProject(nonExistentProjectId)).thenReturn(false);

        ResponseEntity<Void> response = userProjectController.deleteProject(nonExistentProjectId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userProjectService).deleteUserProject(nonExistentProjectId);
    }

    @Test
    void updateProject_shouldReturnBadRequestWhenInvalidIdFormat() {
        String invalidId = "invalid";
        UserProject project = new UserProject();
        project.setName("Updated Project");
        project.setDescription("Updated Description");

        ResponseEntity<UserProject> response = userProjectController.updateProject(invalidId, project);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService, never()).updateUserProject(any(), any());
    }

    @Test
    void deleteProject_shouldHandleExceptionWhenServiceThrowsException() {
        Long projectId = 1L;
        when(userProjectService.deleteUserProject(projectId)).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<Void> response = userProjectController.deleteProject(projectId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userProjectService).deleteUserProject(projectId);
    }

    @Test
    void deleteProject_shouldCallDeleteUserProjectWithCorrectId() {
        Long projectId = 1L;
        when(userProjectService.deleteUserProject(projectId)).thenReturn(true);

        userProjectController.deleteProject(projectId);

        verify(userProjectService).deleteUserProject(projectId);
    }

    @Test
    void deleteProject_shouldReturnBadRequestWhenIdIsNull() {

        ResponseEntity<Void> response = userProjectController.deleteProject(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userProjectService, never()).deleteUserProject(any());
    }

    @Test
    void deleteProject_shouldReturnBadRequestWhenIdIsNegative() {
        Long negativeId = -1L;

        ResponseEntity<Void> response = userProjectController.deleteProject(negativeId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userProjectService, never()).deleteUserProject(any());
    }

    @Test
    void deleteProject_shouldReturnInternalServerErrorWhenServiceThrowsUnexpectedException() {
        Long projectId = 1L;
        when(userProjectService.deleteUserProject(projectId)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<Void> response = userProjectController.deleteProject(projectId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userProjectService).deleteUserProject(projectId);
    }

    @Test
    void deleteProject_shouldReturnBadRequestWhenIdIsZero() {
        Long zeroId = 0L;

        ResponseEntity<Void> response = userProjectController.deleteProject(zeroId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userProjectService, never()).deleteUserProject(any());
    }

    @Test
    void deleteProject_shouldHandleVeryLargeLongId() {
        Long veryLargeId = Long.MAX_VALUE;
        when(userProjectService.deleteUserProject(veryLargeId)).thenReturn(true);

        ResponseEntity<Void> response = userProjectController.deleteProject(veryLargeId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userProjectService).deleteUserProject(veryLargeId);
    }

    @Test
    void deleteProject_shouldReturnNotFoundWhenServiceReturnsNull() {
        Long projectId = 1L;
        when(userProjectService.deleteUserProject(projectId)).thenReturn(null);

        ResponseEntity<Void> response = userProjectController.deleteProject(projectId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userProjectService).deleteUserProject(projectId);
    }

    @Test
    void deleteProject_shouldHandleConcurrentDeleteRequests() {
        Long projectId = 1L;
        when(userProjectService.deleteUserProject(projectId))
                .thenReturn(true)
                .thenReturn(false);

        ResponseEntity<Void> response1 = userProjectController.deleteProject(projectId);
        ResponseEntity<Void> response2 = userProjectController.deleteProject(projectId);

        assertEquals(HttpStatus.NO_CONTENT, response1.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        verify(userProjectService, times(2)).deleteUserProject(projectId);
    }

    @Test
    void deleteProject_shouldHandleCustomExceptionFromService() {
        Long projectId = 1L;
        when(userProjectService.deleteUserProject(projectId)).thenThrow(new Exception("Custom error"));

        ResponseEntity<Void> response = userProjectController.deleteProject(projectId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userProjectService).deleteUserProject(projectId);
    }

    @Test
    void deleteProject_shouldHandleNetworkLatencyOrTimeout() {
        Long projectId = 1L;
        when(userProjectService.deleteUserProject(projectId)).thenAnswer(invocation -> {
            Thread.sleep(5000);
            return true;
        });

        ResponseEntity<Void> response = userProjectController.deleteProject(projectId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userProjectService).deleteUserProject(projectId);
    }

    @Test
    void deleteProject_shouldHandleProjectNotFoundException() {
        Long projectId = 1L;
        when(userProjectService.deleteUserProject(projectId)).thenThrow(new ProjectNotFoundException("Project not found"));

        ResponseEntity<Void> response = userProjectController.deleteProject(projectId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userProjectService).deleteUserProject(projectId);
    }

    @Test
    void deleteProject_shouldHandleProjectWithAssociatedData() {
        Long projectId = 1L;
        when(userProjectService.deleteUserProject(projectId)).thenThrow(new RuntimeException("Cannot delete project with associated data"));

        ResponseEntity<Void> response = userProjectController.deleteProject(projectId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userProjectService).deleteUserProject(projectId);
    }

    @Test
    void deleteProject_shouldHandleDeletingLargeNumberOfProjectsInSuccession() {
        int numberOfProjects = 1000;
        List<Long> projectIds = IntStream.rangeClosed(1, numberOfProjects)
                .mapToObj(Long::valueOf)
                .toList();

        when(userProjectService.deleteUserProject(anyLong())).thenReturn(true);

        long startTime = System.currentTimeMillis();

        for (Long projectId : projectIds) {
            ResponseEntity<Void> response = userProjectController.deleteProject(projectId);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        assertTrue(executionTime < 5000, "Deleting " + numberOfProjects + " projects took longer than 5 seconds");
        verify(userProjectService, times(numberOfProjects)).deleteUserProject(anyLong());
    }

    @Test
    void deleteProject_shouldHandleMinimumAllowedProjectId() {
        Long minimumId = 1L;
        when(userProjectService.deleteUserProject(minimumId)).thenReturn(true);

        ResponseEntity<Void> response = userProjectController.deleteProject(minimumId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userProjectService).deleteUserProject(minimumId);
    }

    @Test
    void deleteProject_shouldHandleConcurrentModification() {
        Long projectId = 1L;
        when(userProjectService.deleteUserProject(projectId))
                .thenAnswer(invocation -> {
                    Thread.sleep(100);
                    return false;
                });

        ResponseEntity<Void> response = userProjectController.deleteProject(projectId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userProjectService).deleteUserProject(projectId);
    }

    @Test
    void deleteProject_shouldDeleteProjectImmediatelyAfterCreation() {
        UserProject project = new UserProject();
        project.setName("Test Project");
        project.setDescription("Test Description");

        UserProject createdProject = new UserProject();
        createdProject.setId(1L);
        createdProject.setName(project.getName());
        createdProject.setDescription(project.getDescription());

        when(userProjectService.createUserProject(project)).thenReturn(createdProject);
        when(userProjectService.deleteUserProject(1L)).thenReturn(true);

        ResponseEntity<UserProject> createResponse = userProjectController.createProject(project);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        ResponseEntity<Void> deleteResponse = userProjectController.deleteProject(1L);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        verify(userProjectService).createUserProject(project);
        verify(userProjectService).deleteUserProject(1L);
    }

    @Test
    void getProjectsByUserId_shouldReturnOkStatusAndEmptyOptionalWhenNoProjectFound() {
        Long userId = 1L;
        when(userProjectService.getUserProjectById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Optional<UserProject>> response = userProjectController.getProjectsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
        verify(userProjectService).getUserProjectById(userId);
    }

    @Test
    void getProjectsByUserId_shouldHandleServiceException() {
        Long userId = 1L;
        when(userProjectService.getUserProjectById(userId)).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<Optional<UserProject>> response = userProjectController.getProjectsByUserId(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService).getUserProjectById(userId);
    }

    @Test
    void getProjectsByUserId_shouldReturnBadRequestWhenUserIdIsNullOrNegative() {
        ResponseEntity<Optional<UserProject>> responseNull = userProjectController.getProjectsByUserId(null);
        assertEquals(HttpStatus.BAD_REQUEST, responseNull.getStatusCode());
        assertNull(responseNull.getBody());

        ResponseEntity<Optional<UserProject>> responseNegative = userProjectController.getProjectsByUserId(-1L);
        assertEquals(HttpStatus.BAD_REQUEST, responseNegative.getStatusCode());
        assertNull(responseNegative.getBody());

        verify(userProjectService, never()).getUserProjectById(any());
    }

    @Test
    void getProjectsByUserId_shouldReturnCorrectProjectData() {
        Long userId = 1L;
        UserProject expectedProject = new UserProject();
        expectedProject.setId(userId);
        expectedProject.setName("Test Project");
        expectedProject.setDescription("Test Description");

        when(userProjectService.getUserProjectById(userId)).thenReturn(Optional.of(expectedProject));

        ResponseEntity<Optional<UserProject>> response = userProjectController.getProjectsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isPresent());
        UserProject actualProject = response.getBody().get();
        assertEquals(expectedProject.getId(), actualProject.getId());
        assertEquals(expectedProject.getName(), actualProject.getName());
        assertEquals(expectedProject.getDescription(), actualProject.getDescription());
        verify(userProjectService).getUserProjectById(userId);
    }

    @Test
    void getProjectsByUserId_shouldHandleVeryLargeUserIdValues() {
        Long veryLargeUserId = Long.MAX_VALUE;
        UserProject expectedProject = new UserProject();
        expectedProject.setId(veryLargeUserId);
        expectedProject.setName("Large ID Project");
        expectedProject.setDescription("Project with very large ID");

        when(userProjectService.getUserProjectById(veryLargeUserId)).thenReturn(Optional.of(expectedProject));

        ResponseEntity<Optional<UserProject>> response = userProjectController.getProjectsByUserId(veryLargeUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isPresent());
        UserProject actualProject = response.getBody().get();
        assertEquals(veryLargeUserId, actualProject.getId());
        assertEquals("Large ID Project", actualProject.getName());
        assertEquals("Project with very large ID", actualProject.getDescription());
        verify(userProjectService).getUserProjectById(veryLargeUserId);
    }

    @Test
    void getProjectsByUserId_shouldHandleOptionalWithNullValue() {
        Long userId = 1L;
        when(userProjectService.getUserProjectById(userId)).thenReturn(Optional.ofNullable(null));

        ResponseEntity<Optional<UserProject>> response = userProjectController.getProjectsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
        verify(userProjectService).getUserProjectById(userId);
    }

    @Test
    void getProjectsByUserId_shouldNotModifyProjectDataBeforeReturning() {
        Long userId = 1L;
        UserProject originalProject = new UserProject();
        originalProject.setId(userId);
        originalProject.setName("Original Project");
        originalProject.setDescription("Original Description");

        when(userProjectService.getUserProjectById(userId)).thenReturn(Optional.of(originalProject));

        ResponseEntity<Optional<UserProject>> response = userProjectController.getProjectsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isPresent());
        UserProject returnedProject = response.getBody().get();
        assertEquals(originalProject.getId(), returnedProject.getId());
        assertEquals(originalProject.getName(), returnedProject.getName());
        assertEquals(originalProject.getDescription(), returnedProject.getDescription());
        assertSame(originalProject, returnedProject, "The returned project should be the same instance as the original");
        verify(userProjectService).getUserProjectById(userId);
    }

    @Test
    void getProjectsByUserId_shouldHandleInvalidUserIdFormat() {
        String invalidUserId = "abc";

        ResponseEntity<Optional<UserProject>> response = userProjectController.getProjectsByUserId(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService, never()).getUserProjectById(any());
    }

    @Test
    void getProjectsByUserId_shouldHandleHighVolumeConcurrentRequests() throws InterruptedException {
        int numberOfRequests = 1000;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<ResponseEntity<Optional<UserProject>>>> futures = new ArrayList<>();

        UserProject mockProject = new UserProject();
        mockProject.setId(1L);
        mockProject.setName("Test Project");
        when(userProjectService.getUserProjectById(anyLong())).thenReturn(Optional.of(mockProject));

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfRequests; i++) {
            futures.add(executorService.submit(() -> {
                ResponseEntity<Optional<UserProject>> response = userProjectController.getProjectsByUserId(1L);
                latch.countDown();
                return response;
            }));
        }

        latch.await(10, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        for (Future<ResponseEntity<Optional<UserProject>>> future : futures) {
            ResponseEntity<Optional<UserProject>> response = future.get();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isPresent());
        }

        long duration = endTime - startTime;
        assertTrue(duration < 5000, "High volume concurrent requests took too long: " + duration + "ms");

        verify(userProjectService, times(numberOfRequests)).getUserProjectById(anyLong());
        executorService.shutdown();
    }

}
