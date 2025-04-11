package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.anyline.repoapi.controller.UserProjectController;
import ru.anyline.repoapi.model.UserProject;
import ru.anyline.repoapi.service.UserProjectServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

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

}
