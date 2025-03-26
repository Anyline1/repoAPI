package ru.anyline.repoapi;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.anyline.repoapi.controller.UserProjectController;
import ru.anyline.repoapi.service.UserProjectServiceImpl;

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

}
