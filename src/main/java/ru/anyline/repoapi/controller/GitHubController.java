package ru.anyline.repoapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import ru.anyline.repoapi.model.UserRepos;
import ru.anyline.repoapi.service.GitHubServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/")
@Tag(
        name = "GitHub public repos",
        description = "API для получения списка публичных репо"
)
public class GitHubController {

    private final GitHubServiceImpl gitHubServiceImpl;

    public GitHubController(GitHubServiceImpl gitHubServiceImpl){
        this.gitHubServiceImpl = gitHubServiceImpl;
    }

    @GetMapping("/repos/{username}")
    @Tag(
            name = "GitHub public repos",
            description = "API для получения списка публичных репо"
    )
    public List<UserRepos> getRepositories(@PathVariable String username) {
        return gitHubServiceImpl.getRepositories(username);
    }

    @GetMapping("/cached")
    @Tag(
            name = "Get all repos from DB",
            description = "Выводит JSON список всех сохраненных репо"
    )
    public List<UserRepos> getAllRepos(){
        return gitHubServiceImpl.getCachedRepos();
    }


}