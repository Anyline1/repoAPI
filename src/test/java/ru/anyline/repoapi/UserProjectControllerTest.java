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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

}
