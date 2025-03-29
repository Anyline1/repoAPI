package ru.anyline.repoapi;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.anyline.repoapi.controller.UserProjectController;
import ru.anyline.repoapi.model.UserProject;
import ru.anyline.repoapi.service.UserProjectServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        Long nonExistentId = 100L;
        when(userProjectService.getUserProjectById(nonExistentId)).thenReturn(Optional.empty());

        ResponseEntity<UserProject> response = userProjectController.getProjectById(nonExistentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userProjectService).getUserProjectById(nonExistentId);
    }

}
