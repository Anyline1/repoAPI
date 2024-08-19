package ru.anyline.repoapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import ru.anyline.repoapi.model.UserRepos;
import ru.anyline.repoapi.service.GitHubService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
    @RestController
    @RequestMapping("/")
    @Tag(
            name = "GitHub public repos",
            description = "API для получения списка публичных репо"
    )
    public class GitHubController {

        private final GitHubService gitHubService;

        public GitHubController(GitHubService gitHubService){
            this.gitHubService = gitHubService;
        }

        @GetMapping("/repos/{username}")
        @Tag(
                name = "GitHub public repos",
                description = "API для получения списка публичных репо"
        )
        public List<UserRepos> getRepositories(@PathVariable String username) {
            return gitHubService.getRepositories(username);
        }

        @GetMapping("/cached")
        @Tag(
                name = "Get all repos from DB",
                description = "Выводит JSON список всех сохраненных репо"
        )
        public List<UserRepos> getAllRepos(){
            return gitHubService.getAllRepos();
        }


    }