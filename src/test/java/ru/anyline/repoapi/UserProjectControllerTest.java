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

}
