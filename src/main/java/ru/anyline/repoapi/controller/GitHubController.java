package ru.anyline.repoapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import ru.anyline.repoapi.model.UserRepos;
import ru.anyline.repoapi.service.GitHubService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
    @RestController
    @RequestMapping("/")
    @Tag(
            name = "GitHub public repos",
            description = "API для получения списка публичных репо"
    )
    public class GitHubController {

        @Autowired
        private GitHubService gitHubService;

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